package sn.service;

import sn.api.response.MessageResponse;
import sn.api.response.tagresponse.TagResponseDTO;

public interface ITagService {

    TagResponseDTO createTag(String tag);

    MessageResponse deleteTagById(long id);
}
