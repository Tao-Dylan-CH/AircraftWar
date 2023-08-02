package dao;

import java.util.List;

/**
 * @author 挚爱之夕
 * @date 2022-02-05 - 02 - 05 - 14:59
 * @Description 查询数据库中表数据
 * @Version 1.0
 */
public class PlayerRecordDao extends BasicDao<PlayerRecord>{
    @Override
    public List<PlayerRecord> queryMultiply(String sql, Class<PlayerRecord> clazz, Object... parameters) {
        return super.queryMultiply(sql, PlayerRecord.class, parameters);
    }
}
