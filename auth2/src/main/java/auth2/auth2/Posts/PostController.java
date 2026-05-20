package auth2.auth2.Posts;

import auth2.auth2.Platforms.MetricDto;
import auth2.auth2.Platforms.PlatformResultDto;
import auth2.auth2.users.Users;
import auth2.auth2.users.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Post")
public class PostController {
   @Autowired
    UsersRepo usersrepo;
@Autowired
PostsService postsService;

    @PostMapping("create")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postrequest){
      Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        Users user =  usersrepo.findByUserEmail(authentication.getName()).orElseThrow(
               ()-> new RuntimeException("User Not found"));
         int userId = user.getId();
         PostResponseDto postResponseDto=  postsService.createPost(userId,postrequest);

         return new ResponseEntity<>(postResponseDto, HttpStatus.CREATED);

    }

    @GetMapping("getAll")
    public ResponseEntity<List<PostResponseDto>>getAllPosts(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        Users user =  usersrepo.findByUserEmail(authentication.getName()).orElseThrow(
                ()-> new RuntimeException("User_Not_Found"));
        int userId = user.getId();
        return postsService.getPosts(userId);
    }

    @PostMapping("Publish")
    public ResponseEntity<List<PlatformResultDto>>publishPost(@RequestParam long postId){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        Users user =  usersrepo.findByUserEmail(authentication.getName())
                .orElseThrow(()-> new RuntimeException("User_Not_Found"));
        int userId = user.getId();
         List<PlatformResultDto> platformResultDto=  postsService.PublishPost(postId,userId);
         return new ResponseEntity<>(platformResultDto,HttpStatus.OK);
    }

    @GetMapping("metrics")
    public ResponseEntity<List<MetricDto>> getPostMetrics(@RequestParam long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersrepo.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User_Not_Found"));
        int userId = user.getId();
        List<MetricDto> metrics = postsService.getPostMetrics(postId, userId);
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }
}
