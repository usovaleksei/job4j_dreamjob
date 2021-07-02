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
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlStore.class)

public class AuthServletTest {

    @Test
    public void whenTrueAuthorization() throws ServletException, IOException {
        Store store = MemStore.instOf();
        store.createUser(new User("Alex", "mail", "pass"));
        PowerMockito.mockStatic(PsqlStore.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        PowerMockito.when(PsqlStore.instOf()).thenReturn(store);
        PowerMockito.when(req.getParameter("email")).thenReturn("mail");
        PowerMockito.when(req.getParameter("password")).thenReturn("pass");
        PowerMockito.when(req.getSession()).thenReturn(session);
        new AuthServlet().doPost(req, resp);
        verify(resp).sendRedirect(req.getContextPath() + "/post/posts.do");
    }

    /*@Test
    public void whenFalseAuthorization() throws ServletException, IOException {
        Store store = MemStore.instOf();
        PowerMockito.mockStatic(PsqlStore.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        PowerMockito.when(PsqlStore.instOf()).thenReturn(store);
        PowerMockito.when(req.getParameter("email")).thenReturn("mail");
        PowerMockito.when(req.getParameter("password")).thenReturn("pass");
        PowerMockito.when(req.getSession()).thenReturn(session);
        PowerMockito.when(req.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);
        new AuthServlet().doPost(req, resp);
        verify(dispatcher).forward(req, resp);
    }*/
}
