package aircrew.version1.service.impl;

import aircrew.version1.entity.*;
import aircrew.version1.mapper.*;
import aircrew.version1.service.CheckService;
import aircrew.version1.utils.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.util.*;

@Service
public class CheckServiceImpl implements CheckService {

    private final AirRepository airRepository;
    private final MpRepository mpRepository;
    private final CadreRepository cadreRepository;

    @Autowired
    CheckServiceImpl(MpRepository mpRepository, AirRepository airRepository,CadreRepository cadreRepository){
        this.mpRepository = mpRepository;
        this.airRepository = airRepository;
        this.cadreRepository = cadreRepository;
    }

    @Override
    public String check(Model model) {
        model.addAttribute("activeUri","check");
        List<Mp> pilotListByOrder = mpRepository.ByOrder();
        List<Mp> lastMpList = mpRepository.LastByOrder();
        List<Mp> nextMpList = mpRepository.NextByOrder();
        if(lastMpList.isEmpty())
            model.addAttribute("msg","上月数据为空");
        if(nextMpList.isEmpty())
            model.addAttribute("msg2"," 下月数据为空");
        List<Mp> lastOneMpList = new ArrayList<>();
        List<Mp> nextOneMpList = new ArrayList<>();
        for(int i= 0;i<lastMpList.size()-1;i++){
            Mp pilot1 = lastMpList.get(i);
            Mp pilot2 = lastMpList.get(i+1);
            if(pilot1.getEid()!=pilot2.getEid()){
                lastOneMpList.add(pilot1);
                continue;
            }
            if(i==lastMpList.size()-2)
                lastOneMpList.add(pilot2);
        }

        for(int i= 0;i<nextMpList.size()-1;i++){
            Mp pilot1 = nextMpList.get(i);
            Mp pilot2 = nextMpList.get(i+1);
            if(i==0)
                nextOneMpList.add(pilot1);
            if(pilot1.getEid()!=pilot2.getEid()){
                nextOneMpList.add(pilot2);
            }
        }

        pilotListByOrder.addAll(lastOneMpList);
        pilotListByOrder.addAll(nextOneMpList);
        String []sortNameArr=new String[]{"eid","date","takeOffTime"};
        boolean [] isAscArr = {true,true,true};
        ListUtils.sort(pilotListByOrder,sortNameArr,isAscArr);
        List<Mp> errorPilotList = new ArrayList<>();
        if(pilotListByOrder.isEmpty()) {
            PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(pilotListByOrder);
            model.addAttribute("errorPilotInfo",errorPilotInfo);
            return "/check/check.html";
        }

        int length = 0;
        int length2 = 0;
        String startTcc ;
        String endTcc ;
        List<Cadre> cadreList = cadreRepository.findAll();
        List<Integer> cadreNumberList = new ArrayList<>();
        for(Cadre cadre:cadreList){
            cadreNumberList.add(cadre.getEid());
        }
        for(int i = 0;i<pilotListByOrder.size()-1;i++){
            Mp pilot1 = pilotListByOrder.get(i);
            Mp pilot2 = pilotListByOrder.get(i+1);
            if (pilot1.getTcc() ==null || pilot2.getTcc() == null) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
            length = pilot1.getTcc().length();
            length2 = pilot2.getTcc().length();
            if (length==0 || length2 == 0) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
            if (pilot2.getEid() != pilot1.getEid()) {
                continue;
            }
            endTcc = pilot1.getTcc().substring(length - 3, length);
            startTcc = pilot2.getTcc().substring(0, 3);
            String cadre1="";
            String cadre2="";
            if (!endTcc.equals(startTcc) ){
                if(cadreNumberList.contains(pilot1.getEid()))
                    cadre1 = "     备注：飞行管理干部";
                if(cadreNumberList.contains(pilot2.getEid()))
                    cadre2 = "     备注：飞行管理干部";
                String []no1 = pilot1.getFlightNo().split("/");
                StringBuilder tcc1= new StringBuilder();
                String date1 = pilot1.getDate();
                int eid1 = pilot1.getEid();
                String ss = "";
                for (String s : no1) {
                    if(ss.equals(s))
                        continue;
                    tcc1.append(airRepository.dep(date1, s, eid1));
                    tcc1.append(airRepository.arr(date1, s, eid1));
                    ss = s;
                }
                String index1 = tcc1.toString();
                String []TCC1 = index1.split("\\[");
                String socTcc1="";
                int fSub = 1;
                for(int sub=0;sub<TCC1.length;sub++){
                    if(TCC1[sub].length()>1) {
                        socTcc1 = socTcc1 + "-" + TCC1[sub].substring(0, 3);
                        if (fSub < TCC1.length) {
                            if (TCC1[sub].equals(TCC1[fSub])) {
                                sub++;
                                fSub++;
                            }
                        }
                    }
                    fSub++;
                }
                if(TCC1[TCC1.length-1].length()>4)
                    socTcc1 = socTcc1 +"-" + TCC1[TCC1.length-1].substring(5,8);
                if(socTcc1.length()>1)
                    socTcc1 = socTcc1.substring(1);
                String Tcc1 = pilot1.getTcc();
                if(!socTcc1.equals(pilot1.getTcc()) &!socTcc1.isEmpty())
                    Tcc1 = Tcc1+"  (Soc数据:"+socTcc1+")";
                String []no2 = pilot2.getFlightNo().split("/");
                StringBuilder tcc2= new StringBuilder();
                String date2 = pilot2.getDate();
                int eid2 = pilot2.getEid();
                String ss2="";
                for (String s : no2) {
                    if(ss2.equals(s))
                        continue;
                    tcc2.append(airRepository.dep(date2, s, eid2));
                    tcc2.append(airRepository.arr(date2, s, eid2));
                    ss2 = s;
                }
                String index2 = tcc2.toString();
                String []TCC2 = index2.split("\\[");
                String socTcc2="";
                int fSub2 = 1;
                for(int sub=0;sub<TCC2.length;sub++){
                    if(TCC2[sub].length()>1) {
                        socTcc2 = socTcc2 + "-" + TCC2[sub].substring(0, 3);
                        if (fSub2 < TCC2.length) {
                            if (TCC2[sub].equals(TCC2[fSub2])) {
                                sub++;
                                fSub2++;
                            }
                        }
                    }
                    fSub2++;
                }
                if(TCC2[TCC2.length-1].length()>4)
                    socTcc2 = socTcc2 +"-" + TCC2[TCC2.length-1].substring(5,8);
                if(socTcc2.length()>1)
                    socTcc2 = socTcc2.substring(1);
                String Tcc2 = pilot2.getTcc();
                if(!socTcc2.equals(pilot2.getTcc()) & !socTcc2.isEmpty())
                    Tcc2 = Tcc2 + "  (Soc数据:"+socTcc2+")";
                if(pilot1.getProperty().equals("调组乘机乘车"))
                    Tcc1 = pilot1.getTcc();
                if(pilot2.getProperty().equals("调组乘机乘车"))
                    Tcc2 = pilot2.getTcc();

                Mp mpCheck1 = new Mp(pilot1.getDate()+" "+pilot1.getTakeOffTime(),pilot1.getEid(),pilot1.getName(),pilot1.getAirLine(),Tcc1,pilot1.getFlightNo(),pilot1.getProperty()+cadre1);
                errorPilotList.add(mpCheck1);

                Mp mpCheck2 = new Mp(pilot2.getDate()+" "+pilot2.getTakeOffTime(),pilot2.getEid(),pilot2.getName(),pilot2.getAirLine(),Tcc2,pilot2.getFlightNo(),pilot2.getProperty()+cadre2);
                errorPilotList.add(mpCheck2);

            }
        }
        for(int i=1;i<errorPilotList.size()-1;i++){
            if (errorPilotList.get(i).equals(errorPilotList.get(i+1))){
                errorPilotList.remove(i);
                i = i-1;
            }
        }
        PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(errorPilotList);
        model.addAttribute("errorPilotInfo",errorPilotInfo);
        return "/check/check.html";
    }

    @Override
    public String socCheck(Model model) {
        model.addAttribute("activeUri","socCheck");
        List<Mp> pilotListByOrder = mpRepository.ByOrder();
        List<Mp> lastMpList = mpRepository.LastByOrder();
        List<Mp> nextMpList = mpRepository.NextByOrder();
        if(lastMpList.isEmpty())
            model.addAttribute("msg","上月数据为空");
        if(nextMpList.isEmpty())
            model.addAttribute("msg2"," 下月数据为空");
        List<Mp> lastOneMpList = new ArrayList<>();
        List<Mp> nextOneMpList = new ArrayList<>();
        for(int i= 0;i<lastMpList.size()-1;i++){
            Mp pilot1 = lastMpList.get(i);
            Mp pilot2 = lastMpList.get(i+1);
            if(pilot1.getEid()!=pilot2.getEid()){
                lastOneMpList.add(pilot1);
                continue;
            }
            if(i==lastMpList.size()-2)
                lastOneMpList.add(pilot2);
        }

        for(int i= 0;i<nextMpList.size()-1;i++){
            Mp pilot1 = nextMpList.get(i);
            Mp pilot2 = nextMpList.get(i+1);
            if(i==0)
                nextOneMpList.add(pilot1);
            if(pilot1.getEid()!=pilot2.getEid()){
                nextOneMpList.add(pilot2);
            }
        }

        pilotListByOrder.addAll(lastOneMpList);
        pilotListByOrder.addAll(nextOneMpList);
        String []sortNameArr=new String[]{"eid","date","takeOffTime"};
        boolean [] isAscArr = {true,true,true};
        ListUtils.sort(pilotListByOrder,sortNameArr,isAscArr);
        List<Mp> errorPilotList = new ArrayList<>();
        if(pilotListByOrder.isEmpty()) {
            PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(pilotListByOrder);
            model.addAttribute("errorPilotInfo",errorPilotInfo);
            return "/check/check.html";
        }

        int length = 0;
        int length2 = 0;
        String startTcc ;
        String endTcc ;
        List<Cadre> cadreList = cadreRepository.findAll();
        List<Integer> cadreNumberList = new ArrayList<>();
        for(Cadre cadre:cadreList){
            cadreNumberList.add(cadre.getEid());
        }
        for(int i = 0;i<pilotListByOrder.size()-1;i++){
            Mp pilot1 = pilotListByOrder.get(i);
            Mp pilot2 = pilotListByOrder.get(i+1);
            if (pilot1.getTcc() ==null || pilot2.getTcc() == null) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
            length = pilot1.getTcc().length();
            length2 = pilot2.getTcc().length();
            if (length==0 || length2 == 0) {//长度为零时进入下一次判断，防止被调机、正班以外的情况干扰
                continue;
            }
            if (pilot2.getEid() != pilot1.getEid()) {
                continue;
            }
            endTcc = pilot1.getTcc().substring(length - 3, length);
            startTcc = pilot2.getTcc().substring(0, 3);
            String cadre1="";
            String cadre2="";
            if (!endTcc.equals(startTcc) ){
                if(cadreNumberList.contains(pilot1.getEid()))
                    cadre1 = " 备注：飞行管理干部";
                if(cadreNumberList.contains(pilot2.getEid()))
                    cadre2 = " 备注：飞行管理干部";
                String []no1 = pilot1.getFlightNo().split("/");
                StringBuilder tcc1= new StringBuilder();
                String date1 = pilot1.getDate();
                int eid1 = pilot1.getEid();
                String ss = "";
                for (String s : no1) {
                    if(ss.equals(s))
                        continue;
                    tcc1.append(airRepository.dep(date1, s, eid1));
                    tcc1.append(airRepository.arr(date1, s, eid1));
                    ss = s;
                }
                String index1 = tcc1.toString();
                String []TCC1 = index1.split("\\[");
                String socTcc1="";
                int fSub = 1;
                for(int sub=0;sub<TCC1.length;sub++){
                    if(TCC1[sub].length()>1) {
                        socTcc1 = socTcc1 + "-" + TCC1[sub].substring(0, 3);
                        if (fSub < TCC1.length) {
                            if (TCC1[sub].equals(TCC1[fSub])) {
                                sub++;
                                fSub++;
                            }
                        }
                    }
                    fSub++;
                }
                if(TCC1[TCC1.length-1].length()>4)
                    socTcc1 = socTcc1 +"-" + TCC1[TCC1.length-1].substring(5,8);
                if(socTcc1.length()>1)
                    socTcc1 = socTcc1.substring(1);
                String Tcc1 = pilot1.getTcc();
                if(!socTcc1.equals(pilot1.getTcc()) &!socTcc1.isEmpty())
                    Tcc1 = socTcc1;
                String []no2 = pilot2.getFlightNo().split("/");
                StringBuilder tcc2= new StringBuilder();
                String date2 = pilot2.getDate();
                int eid2 = pilot2.getEid();
                String ss2="";
                for (String s : no2) {
                    if(ss2.equals(s))
                        continue;
                    tcc2.append(airRepository.dep(date2, s, eid2));
                    tcc2.append(airRepository.arr(date2, s, eid2));
                    ss2 = s;
                }
                String index2 = tcc2.toString();
                String []TCC2 = index2.split("\\[");
                String socTcc2="";
                int fSub2 = 1;
                for(int sub=0;sub<TCC2.length;sub++){
                    if(TCC2[sub].length()>1) {
                        socTcc2 = socTcc2 + "-" + TCC2[sub].substring(0, 3);
                        if (fSub2 < TCC2.length) {
                            if (TCC2[sub].equals(TCC2[fSub2])) {
                                sub++;
                                fSub2++;
                            }
                        }
                    }
                    fSub2++;
                }
                if(TCC2[TCC2.length-1].length()>4)
                    socTcc2 = socTcc2 +"-" + TCC2[TCC2.length-1].substring(5,8);
                if(socTcc2.length()>1)
                    socTcc2 = socTcc2.substring(1);
                String Tcc2 = pilot2.getTcc();
                if(!socTcc2.equals(pilot2.getTcc()) & !socTcc2.isEmpty())
                    Tcc2 = socTcc2;
                if(pilot1.getProperty().equals("调组乘机乘车"))
                    Tcc1 = pilot1.getTcc();
                if(pilot2.getProperty().equals("调组乘机乘车"))
                    Tcc2 = pilot2.getTcc();
                Mp mpCheck1 = new Mp(pilot1.getDate()+" "+pilot1.getTakeOffTime(),pilot1.getEid(),pilot1.getName(),pilot1.getAirLine(),Tcc1,pilot1.getFlightNo(),pilot1.getProperty()+cadre1);
                errorPilotList.add(mpCheck1);
                Mp mpCheck2 = new Mp(pilot2.getDate()+" "+pilot2.getTakeOffTime(),pilot2.getEid(),pilot2.getName(),pilot2.getAirLine(),Tcc2,pilot2.getFlightNo(),pilot2.getProperty()+cadre2);
                errorPilotList.add(mpCheck2);
            }
        }
        for(int i=1;i<errorPilotList.size()-1;i++){
            if (errorPilotList.get(i).equals(errorPilotList.get(i+1))){
                errorPilotList.remove(i);
                i = i-1;
            }
        }

        List<Mp> socPilotList = new ArrayList<>();
        for(int i = 0;i<errorPilotList.size()-1;i++){
            Mp mp1 = errorPilotList.get(i);
            Mp mp2 = errorPilotList.get(i+1);
            if(mp1.getEid()!=mp2.getEid())
                continue;
            if(!mp1.getTcc().substring(mp1.getTcc().length() - 3).equals(mp2.getTcc().substring(0,3))){
                socPilotList.add(mp1);
                socPilotList.add(mp2);
            }
        }

        for(int i=1;i<socPilotList.size()-1;i++){
            if (socPilotList.get(i).equals(socPilotList.get(i+1))){
                socPilotList.remove(i);
                i = i-1;
            }
        }
        PageInfo<Mp> errorPilotInfo = new PageInfo<Mp>(socPilotList);
        model.addAttribute("errorPilotInfo",errorPilotInfo);
        return "/check/socCheck.html";
    }
}
