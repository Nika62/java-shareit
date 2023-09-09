package shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommentMapper {

 public Comment convertCommentDtoToComment(CommentDto commentDto) {
      if (Objects.isNull(commentDto)) {
          return null;
      }

      Comment comment = new Comment();
      comment.setText(commentDto.getText());
      comment.setCreated(commentDto.getCreated());

      return comment;

 }

 public CommentDto convertCommentToCommentDto(Comment comment) {
         if (Objects.isNull(comment)) {
             return null;
         }
          CommentDto commentDto = new CommentDto();
          commentDto.setId(comment.getId());
          commentDto.setText(comment.getText());
          commentDto.setAuthorName(comment.getAuthor().getName());
          commentDto.setCreated(comment.getCreated());

          return commentDto;
    }

}
