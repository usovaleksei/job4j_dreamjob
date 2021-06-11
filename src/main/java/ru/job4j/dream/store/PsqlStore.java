package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(new FileReader("db.properties"))) {
            cfg.load(io);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        this.pool.setUrl(cfg.getProperty("jdbc.url"));
        this.pool.setUsername(cfg.getProperty("jdbc.username"));
        this.pool.setPassword(cfg.getProperty("jdbc.password"));
        this.pool.setMinIdle(5);
        this.pool.setMaxIdle(10);
        this.pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from post")) {
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getTimestamp("created").toLocalDateTime()));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from candidate")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidates.add(new Candidate(rs.getInt("id"), rs.getString("name"), rs.getString("photoId")));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return candidates;
    }

    @Override
    public void savePost(Post post) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("insert into post(name, description, created) values (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreated()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("insert into candidate(name) values (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Post findPostById(int id) {
        Post post = new Post(id, null, null, null);
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select id, name, description, created from post where id = (?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post.setName(rs.getString("name"));
                    post.setDescription(rs.getString("description"));
                    post.setCreated(rs.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return post;
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate candidate = new Candidate(id, null);
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select id, name from candidate where id = (?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    candidate.setName(rs.getString("name"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return candidate;
    }
}
