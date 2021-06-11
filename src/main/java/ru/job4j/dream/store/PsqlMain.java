package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.time.LocalDateTime;

public class PsqlMain {

    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.savePost(new Post("Java junior", "Write java code", LocalDateTime.now()));
        store.savePost(new Post("Java middle", "Write middle java code", LocalDateTime.now()));
        for (Post post : store.findAllPosts()) {
            System.out.println("id: " + post.getId() + " name: " + post.getName() + " description: " + post.getDescription() + " created: " + post.getCreated());
        }

        store.saveCandidate(new Candidate("Aleksei Java"));
        store.saveCandidate(new Candidate("Roman Scala"));
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println("id: " + candidate.getId() + " name: " + candidate.getName() );
        }

        System.out.println("Find post by ID: " + store.findPostById(19));

        System.out.println("Find candidate by ID: " + store.findCandidateById(17));
    }
}
