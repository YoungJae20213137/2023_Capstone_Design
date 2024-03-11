package ji.vegan.backend.config.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestMatcher {

    private final String path;
    private final String[] methods;
    private boolean role;
    private String[] roles;


    public RequestMatcher(String path) {
        this.path = path;
        this.methods = new String[]{"GET", "PUT", "PATCH", "POST", "DELETE"};
    }

    public RequestMatcher(String path, final String... method) {
        this.path = path;
        this.methods = method;
    }

    public RequestMatcher permitAll() {
        this.role = false;
        return this;
    }

    public RequestMatcher hasAnyRole(final String... roles) {
        this.role = true;
        this.roles = roles;
        return this;
    }


}
