package auth2.auth2.Platforms.meta.metaDto;

import java.util.List;

public class MetaPagesResponseDto {
    private List<PageData> data;

    public MetaPagesResponseDto(List<PageData> data) {
        this.data = data;
    }

    public MetaPagesResponseDto() {
    }

    public List<PageData> getData() {
        return data;
    }

    public void setData(List<PageData> data) {
        this.data = data;
    }

    public static class PageData {
        private String id; // page_id
        private String name;
        private String access_token; // VERY IMPORTANT (page token)

        // getters & setters

        public PageData(String id, String name, String access_token) {
            this.id = id;
            this.name = name;
            this.access_token = access_token;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAccess_token() {
            return access_token;
        }

        public PageData() {
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
    }
}
