package sn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.model.Post;
import sn.repositories.PostRepository;
import sn.service.PostNotFoundException;
import sn.service.PostService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public Post findById(long postId) throws PostNotFoundException {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Comment not fount by id = " + postId));
    }

    @Override
    public List<Post> findAllByPersonId(long personId, int offset, int itemPerPage) {
        int pageNumber = offset / itemPerPage;
        Sort sort = Sort.by(Sort.Direction.DESC, PostRepository.POST_TIME);
        Pageable pageable = PageRequest.of(pageNumber, itemPerPage, sort);
        return postRepository.findAllByPersonId(personId, pageable);
    }

    @Override
    public Post addPost(Person author, String title, String text, LocalDateTime postTime) {
        Post post = new Post();
        post.setTime(postTime);
        post.setAuthor(author);
        post.setTitle(title);
        post.setText(text);
        return postRepository.saveAndFlush(post);
    }

    @Override
    public int getTotalCountPostsByPersonId(long personId) {
        return postRepository.getTotalCountPostsByPersonId(personId);
    }

}
