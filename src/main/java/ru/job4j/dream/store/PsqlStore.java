package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

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

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(new FileReader("db.properties"))) {
            cfg.load(io);
        } catch (IOException e) {
            LOG.error("Error reading database settings", e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            LOG.error("Error loading database driver", e);
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
    public Post findPostById(int id) {
        Post post = null;
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select id, name, description, created from post where id = (?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post = new Post(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return post;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from post")) {
            try (
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("created").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return posts;
    }

    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            createPost(post);
        } else {
            updatePost(post);
        }
    }

    @Override
    public void createPost(Post post) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("insert into post(name, description, created) values (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreated()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
    }

    @Override
    public void updatePost(Post post) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update post set name = (?) where id = (?)")) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate candidate = null;
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select id, name from candidate where id = (?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    candidate = new Candidate(rs.getInt("id"),
                            rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return candidate;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from candidate")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidates.add(new Candidate(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("cityId"),
                            rs.getString("photoId")));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return candidates;
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            createCandidate(candidate);
        } else {
            updateCandidate(candidate);
        }
    }

    @Override
    public void createCandidate(Candidate candidate) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("insert into candidate(name, cityid) values (?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCityId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                    candidate.setPhotoId(id.getString(1));
                    try (PreparedStatement ps1 = cn.prepareStatement("update candidate set photoId = (?) where id = (?)")) {
                        ps1.setString(1, id.getString(1));
                        ps1.setInt(2, id.getInt(1));
                        ps1.execute();
                    }
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
    }

    @Override
    public void updateCandidate(Candidate candidate) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update candidate set name = (?) where id = (?)")) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
    }

    @Override
    public void deleteCandidateById(int id) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from candidate where id = (?)")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
    }

    @Override
    public void createUser(User user) {
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("insert into users(name, email, password) values(?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        User user = null;
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select id, name, email, password from users where email = (?)")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return user;
    }

    @Override
    public List<City> findAllCities() {
        List<City> listCities = new ArrayList<>();
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from cities")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listCities.add(new City(rs.getInt("id"),
                            rs.getString("city")));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return listCities;
    }

    @Override
    public City findCityById(int id) {
        City city = null;
        try (Connection cn = this.pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select id, city from cities where id = (?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    city = new City(rs.getInt("id"),
                            rs.getString("city"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Request execution error", e);
        }
        return city;
    }
}
