package ru.job4j.dream.servlet;

import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PhotoDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Store.instOf().deleteCandidateById(Integer.parseInt(id));
        for (File file : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
            if (file.getName().substring(0, file.getName().lastIndexOf(".")).equals(id)) {
                file.delete();
                break;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/candidate/candidates.do");
    }
}
