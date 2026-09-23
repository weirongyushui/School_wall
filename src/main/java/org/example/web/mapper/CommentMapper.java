package org.example.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.web.entity.Comment;

import java.util.List;

/**
 * 评论表 MyBatis Mapper。
 */
@Mapper
public interface CommentMapper {

    Comment getById(@Param("id") Long id);

    List<Comment> getTopLevelList(@Param("postId") Long postId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    int getTopLevelCount(@Param("postId") Long postId);

    List<Comment> getReplyList(@Param("parentId") Long parentId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    int getReplyCount(@Param("parentId") Long parentId);

    int insert(Comment comment);

    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    int deleteRepliesByParentId(@Param("parentId") Long parentId);

    int hideById(@Param("id") Long id);

    int updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    int updateReplyCount(@Param("id") Long id, @Param("delta") int delta);
}
