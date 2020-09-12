package sn.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.api.response.MessageResponse;
import sn.api.response.tagresponse.TagResponseDTO;
import sn.model.Tag;
import sn.repositories.TagRepository;
import sn.service.ITagService;

@Service
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagResponseDTO createTag(String tag) {
        Tag newTag = new Tag(tag);
        tagRepository.save(newTag);
        return new TagResponseDTO(newTag.getId(), newTag.getTag());
    }

    @Override
    public MessageResponse deleteTagById(long id) {
        MessageResponse response = new MessageResponse();
        if (tagRepository.findById(id).isPresent()) {
            tagRepository.deleteById(id);
            response.setMessage("ok");
        } else {
            response.setMessage("No Such Tag");
        }
        return response;
    }
}
