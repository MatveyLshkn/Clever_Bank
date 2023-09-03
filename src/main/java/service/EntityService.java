package service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface EntityService <T>{
    List<T> get(HttpServletRequest req);

    String save(HttpServletRequest req);

    String delete(HttpServletRequest req);

    String update(HttpServletRequest req);
}
