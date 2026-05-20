package auth2.auth2.Posts;

import auth2.auth2.Platforms.MetricDto;
import auth2.auth2.Platforms.PlatformResultDto;
import auth2.auth2.Platforms.PlatformsServiceInterface;
import auth2.auth2.Platforms.linkedln.linkedinServices.linkedinPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostsService {
@Autowired
PostRepository postRepository;
@Autowired
private List<PlatformsServiceInterface> platformServices;
@Autowired
PlatformPostRepository platformPostRepository;
    public PostResponseDto createPost(int userId,PostRequestDto requestDto){
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(requestDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        // CASE 1: Scheduled post
        if (requestDto.getScheduledAt() != null &&
                requestDto.getScheduledAt().isAfter(LocalDateTime.now())) {
            post.setPostStatus(PostStatus.SCHEDULED);
            post.setScheduledAt(requestDto.getScheduledAt());
        }
        // CASE 2: Immediate post
        else {
            post.setPostStatus(PostStatus.PENDING);}
        postRepository.save(post);
        setPlatformPost(requestDto, post);
        return mapToResponse(post);

    }

    public List<PlatformResultDto> PublishPost(long postId, int userId){

       Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("Post not found"));

           if( post.getUserId() != userId){
               throw new RuntimeException("Unauthorized");
           }
           if(post.getPostStatus()==PostStatus.PUBLISHED){
               throw new RuntimeException("already published");
           }

           List<PlatformPost> platformPostslist = platformPostRepository.findByPost_id(postId);

             List<PlatformResultDto> platformResultDtoList = new ArrayList<>();
            for( PlatformPost platformPost: platformPostslist) {
                for (PlatformsServiceInterface serviceInterface : platformServices) {

                    if (serviceInterface.getPlatform() == platformPost.getPlatform()) {
                        platformPost.setPublishedAt(LocalDateTime.now());
                        PlatformResultDto platformResultDto = serviceInterface.Publish(post, userId);
                        if (platformResultDto.isSuccess()) {
                            platformPost.setPostStatus(PostStatus.PUBLISHED);
                            platformPost.setExternalPostId(platformResultDto.getExternalPostId());
                            //post.setPostStatus(PostStatus.PUBLISHED);
                        } else {
                            platformPost.setErrorMessage(platformResultDto.getErrorMessage());
                            platformPost.setPostStatus(PostStatus.FAILED);
                        }
                        platformPostRepository.save(platformPost);
                        platformResultDtoList.add(platformResultDto);
                    }
                }
            }
           post.setPostStatus(PostStatus.PUBLISHED);
            postRepository.save(post);
           return platformResultDtoList;

    }


    public PostResponseDto mapToResponse(Post post){
        PostResponseDto responseDto = new PostResponseDto();
        responseDto.setPostId(post.getId());
        responseDto.setContent(post.getContent());
        responseDto.setStatus(post.getPostStatus().name());
        responseDto.setCreatedAt(post.getCreatedAt());
        return responseDto;
    }

    public ResponseEntity<List<PostResponseDto>> getPosts(int userId){
        List<PostResponseDto> allPosts = new ArrayList<>();
        allPosts = postRepository.findByUserId(userId).stream()
                .map(this::mapToResponse).toList();
        return new ResponseEntity<>(allPosts,HttpStatus.OK);
    }

    public void setPlatformPost( PostRequestDto requestDto,Post post){

        for (Platforms platform : requestDto.getPlatformsList()) {
            PlatformPost platformPost = new PlatformPost();
            platformPost.setPlatform(platform);
            platformPost.setPost(post);
            platformPost.setPostStatus(PostStatus.PENDING);
            platformPostRepository.save(platformPost);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkScheduledPosts() {

        List<Post> duePosts =
                postRepository.findByPostStatusAndScheduledAtBefore(
                        PostStatus.SCHEDULED,
                        LocalDateTime.now()
                );

        for (Post post : duePosts) {
            PublishPost(post.getId(), post.getUserId());

            post.setPostStatus(PostStatus.PUBLISHED);
            postRepository.save(post);
        }
    }

    public List<MetricDto> getPostMetrics(long postId, int userId) {
        List<PlatformPost> platformPosts = platformPostRepository.findByPost_id(postId);
        List<MetricDto> metricsList = new ArrayList<>();

        for (PlatformPost platformPost : platformPosts) {
            if (platformPost.getPostStatus() == PostStatus.PUBLISHED && platformPost.getExternalPostId() != null) {
                for (PlatformsServiceInterface service : platformServices) {
                    if (service.getPlatform() == platformPost.getPlatform()) {
                        metricsList.add(service.getMetrics(platformPost.getExternalPostId(), userId));
                    }
                }
            }
        }
        return metricsList;
    }
}
