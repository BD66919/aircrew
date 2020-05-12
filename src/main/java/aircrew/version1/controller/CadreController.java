package aircrew.version1.controller;

import aircrew.version1.entity.Cadre;
import aircrew.version1.mapper.CadreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CadreController {
    @Autowired
    CadreRepository cadreRepository;

    private static boolean isNumeric(String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
            {
            return false;
            }
        }
        return true;
    }

    @GetMapping(value = "/cadre")
    public String cadre(Model model){
        List<Cadre> cadreList = cadreRepository.findAll();
        model.addAttribute("cadreList",cadreList);
        return "/pilot/cadre.html";
    }

    @PostMapping(value = "/Cadre")
    @ResponseBody
    public Map<String,Object> addCadre(HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        String eid = request.getParameter("eid");
            if( eid.equals("")){
                map.put("msg","添加失败：员工编号不可为空");
                return map;
            }
            if(!isNumeric(eid)){
                map.put("msg","添加失败：员工编号需为数字");
                return map;
            }
            if(cadreRepository.findByEid(Integer.parseInt(eid))!=null){
                map.put("msg","添加失败：该员工编号已存在数据库中");
                return map;
            }
        String name = request.getParameter("name");
            if( name.equals("")){
                map.put("msg","添加失败：姓名不可为空");
                return map;
            }
        String department = request.getParameter("department");
            if( department.equals("")){
                map.put("msg","添加失败：部门不可为空");
                return map;
            }
        String qualify = request.getParameter("qualify");
        String category = request.getParameter("category");
        if( category.equals("")){
            map.put("msg","添加失败：空勤人员类别不可为空");
            return map;
        }
        Cadre cadre = new Cadre();
        cadre.setEid(Integer.parseInt(eid));
        cadre.setName(name);
        cadre.setDepartment(department);
        cadre.setQualify(qualify);
        cadre.setCategory(category);
        cadreRepository.save(cadre);
        map.put("msg","添加成功");
        return map;
    }

    @PostMapping(value = "/updateCadre")
    @ResponseBody
    public Map<String,Object> updateCadre(HttpServletRequest request){
        System.out.println("ok");
        Map<String,Object> map = new HashMap<>();
        String eid = request.getParameter("eid");
        String name = request.getParameter("name");
        if( name.equals("")){
            map.put("msg","修改失败：姓名不可为空");
            return map;
        }
        String department = request.getParameter("department");
        if( department.equals("")){
            map.put("msg","修改失败：部门不可为空");
            return map;
        }
        String qualify = request.getParameter("qualify");
        String category = request.getParameter("category");
        if( category.equals("")){
            map.put("msg","修改失败：空勤人员类别不可为空");
            return map;
        }
        Cadre cadre = new Cadre();
        int id = cadreRepository.findByEid(Integer.parseInt(eid)).getId();
        cadreRepository.deleteById(id);
        cadre.setEid(Integer.parseInt(eid));
        cadre.setName(name);
        cadre.setDepartment(department);
        cadre.setQualify(qualify);
        cadre.setCategory(category);
        cadreRepository.save(cadre);
        map.put("msg","修改成功");
        return map;
    }

    @PostMapping(value = "/deleteCadre={id}")
    public String deleteCadre(HttpServletRequest request, @PathVariable String id){
        cadreRepository.deleteById(Integer.parseInt(id));
        return "redirect:/cadre";
    }



}
