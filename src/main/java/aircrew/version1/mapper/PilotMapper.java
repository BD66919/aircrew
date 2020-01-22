package aircrew.version1.mapper;

import aircrew.version1.entity.Excel;
import aircrew.version1.entity.Pilot;
import org.apache.ibatis.annotations.Options;

import java.util.List;

public interface PilotMapper {
    public Pilot getById(Integer id);

    public List<Pilot> list();

    @Options(useGeneratedKeys = true,keyProperty = "id")
    public void insert(Pilot pilot);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    public void insertByExcel(Integer id, Excel excel, int error);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    public void listInsertExcel(Integer id,List<Excel> list,int error);

    public void deleteById(Integer id);

    public void updateById(Pilot pilot);

    public void delete();

    public Integer getLastId();

    public List<Pilot> ByOrder();

}
