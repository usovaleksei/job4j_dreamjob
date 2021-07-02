package ru.job4j.dream.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dream.model.User;
import ru.job4j.dream.store.MemStore;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlStore.class)

public class RegServletTest {

    @Test
    public void whenNewUserRegSuccessful() throws ServletException, IOException {
        Store store = MemStore.instOf();
        PowerMockito.mockStatic(PsqlStore.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        PowerMockito.when(req.getParameter("name")).thenReturn("name");
        PowerMockito.when(req.getParameter("email")).thenReturn("mail");
        PowerMockito.when(req.getParameter("password")).thenReturn("pass");
        PowerMockito.when(PsqlStore.instOf()).thenReturn(store);
        PowerMockito.when(req.getRequestDispatcher("/login.jsp")).thenReturn(dispatcher);
        new RegServlet().doPost(req, resp);
        verify(dispatcher).forward(req, resp);
    }

    @Test
    public void whenUserAlreadyExist() throws ServletException, IOException {
        Store store = MemStore.instOf();
        store.createUser(new User("Alex", "mail", "pass"));
        PowerMockito.mockStatic(PsqlStore.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        PowerMockito.when(req.getParameter("name")).thenReturn("name");
        PowerMockito.when(req.getParameter("email")).thenReturn("mail");
        PowerMockito.when(req.getParameter("password")).thenReturn("pass");
        PowerMockito.when(PsqlStore.instOf()).thenReturn(store);
        PowerMockito.when(req.getRequestDispatcher("reg.jsp")).thenReturn(dispatcher);
        new RegServlet().doPost(req, resp);
        verify(dispatcher).forward(req, resp);
    }
}
