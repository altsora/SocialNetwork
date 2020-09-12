package sn.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.api.response.MessageResponse;
import sn.api.response.SimpleServiceResponse;
import sn.api.response.ServiceResponseDataList;
import sn.api.response.tagresponse.TagResponseDTO;
import sn.service.ITagService;

@RestController
@RequestMapping("/tags")
public class TagController {

    private ITagService tagService;

    @Autowired
    public TagController(ITagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ServiceResponseDataList<TagResponseDTO> getTags(
        @RequestParam String tag,
        @RequestParam int offset,
        @RequestParam int initPerPage) {

        TagResponseDTO tagResponseDTO = new TagResponseDTO(tag);

        return new ServiceResponseDataList<>(0, offset, initPerPage, List.of(tagResponseDTO));
    }

    @PostMapping
    public SimpleServiceResponse<TagResponseDTO> createTag(String tag) {
        return new SimpleServiceResponse<>(tagService.createTag(tag));
    }

    @DeleteMapping
    public SimpleServiceResponse<MessageResponse> deleteTag(@RequestParam long id) {
        return new SimpleServiceResponse<>(tagService.deleteTagById(id));
    }
}
