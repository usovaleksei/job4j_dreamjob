package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.Store;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoUploadServlet extends HttpServlet {

    //method uploads the selected file to the server
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Создаем фабрику по которой можем понять, какие данные есть в запросы. Данные могу быть: поля или файлы.
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            //Получаем список всех данных в запросе.
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("c:\\images\\");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                String id = req.getParameter("id");
                if (!item.isFormField()) {
                    //Если элемент не поле, то это файл и из него можно прочитать весь входной поток и записать его в файл или напрямую в базу данных.
                    File file = new File(folder + File.separator + id + item.getName().substring(item.getName().lastIndexOf(".")));
                    //System.out.println("Абсолютный путь: " + file.getAbsolutePath());
                    Candidate candidate = Store.instOf().findCandidateById(Integer.parseInt(id));
                    candidate.setPhotoId(id);
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/candidate/candidates.do");
    }
}
