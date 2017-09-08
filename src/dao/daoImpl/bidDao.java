package dao.daoImpl;

import DO.bid;
import dao.bidMapper;
import org.apache.ibatis.session.SqlSession;
import util.DBTools;

import java.util.List;

/**
 * Created by H77 on 2017/6/13.
 */
public class bidDao {
    public static boolean insert(bid result){
        SqlSession sqlSession = DBTools.getSession();
        bidMapper bids = sqlSession.getMapper(dao.bidMapper.class);
        bids.insert(result);
        sqlSession.commit();
        return true;
    }

    public static List<bid> selectAllBids(){
        SqlSession sqlSession = DBTools.getSession();
        bidMapper bids = sqlSession.getMapper(dao.bidMapper.class);
        return bids.selectAllBid();
    }


}
