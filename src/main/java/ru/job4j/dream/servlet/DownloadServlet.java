package ru.job4j.dream.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("photoId");
        File downloadFile = null;
        for (File file : Objects.requireNonNull(new File("C:\\images\\").listFiles())) {
            //System.out.println(file.getAbsolutePath());
            if (name.equals(file.getName().substring(0, file.getName().lastIndexOf(".")))) {
                downloadFile = file;
                break;
            }
        }
        resp.setContentType("application/octet-stream");
        //Выставляем заголовок ответа в протоколе. Таким образом мы сообщаем браузеру, что будем отправлять файл.
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + Objects.requireNonNull(downloadFile).getName() + "\"");
        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            //Открываем поток и записываем его в выходной поток servlet.
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }
}
