package aircrew.version1.entity;
import java.util.List;

/**
 * @author Jiangzhendong
 * @Description 该类为excel的实体类
 *
 */
public class Excel {
    private String name;
    private int eid;
    private String line;
    private String tcc;
    private String properties;
    private String date;
    private String path; //Excel写入的路径名
    private String sheetName; //表名称（Excel的左下角）
    private int row; //行数
    private int col; //列数
    private List<String> titleList; //列表头标题
    private int[] width; //每列所占宽度
    private List dataList; //数据集合

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTcc() {
        return tcc;
    }

    public void setTcc(String tcc) {
        this.tcc = tcc;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public int[] getWidth() {
        return width;
    }

    public void setWidth(int[] width) {
        this.width = width;
    }

    public List getDataList() {
        return dataList;
    }

    public void setDataList(List dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString(){
        return "Pilot{" +
                "name=" + name +
                ", eid=" + eid +
                ", line=" + line +
                ", tcc=" + tcc +
                ", properties=" + properties +
                ", date=" + date +
                '}';
    }

}
