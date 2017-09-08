package dao;

import DO.bid;

import java.util.List;

public interface bidMapper {
    int deleteByPrimaryKey(Integer bidid);

    int insert(bid record);

    int insertSelective(bid record);

    bid selectByPrimaryKey(Integer bidid);

    int updateByPrimaryKeySelective(bid record);

    int updateByPrimaryKey(bid record);

    List<bid> selectAllBid();
}