package dao.daoImpl;

import DO.tender;
import dao.tenderMapper;
import org.apache.ibatis.session.SqlSession;
import util.DBTools;

import java.util.List;

/**
 * Created by H77 on 2017/6/4.
 */
public class tenderDao {

    public static boolean saveTender(tender tend){
        SqlSession sqlSession = DBTools.getSession();
        tenderMapper tenders = sqlSession.getMapper(dao.tenderMapper.class);
        tenders.insert(tend);
        sqlSession.commit();
        return true;
    }

    public static List<tender> findTenders(int userId , String client){
        SqlSession sqlSession = DBTools.getSession();
        tenderMapper tenders = sqlSession.getMapper(dao.tenderMapper.class);
        return tenders.selectByUserIdAndClient(userId,client);
    }

}
