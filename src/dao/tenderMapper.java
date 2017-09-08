package dao;

import DO.tender;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface tenderMapper {
    int deleteByPrimaryKey(Integer tenderid);

    int insert(tender record);

    int insertSelective(tender record);

    tender selectByPrimaryKey(Integer tenderid);

    int updateByPrimaryKeySelective(tender record);

    int updateByPrimaryKey(tender record);

    List<tender>  selectByUserIdAndClient(@Param("userId")Integer userId, @Param("client") String client);
}