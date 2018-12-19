package com.zoe.controller;


import com.alibaba.fastjson.JSONObject;
import com.zoe.pojo.Member;
import com.zoe.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 告诉spring mvc这是一个控制器类
@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberService memberService;

    @RequestMapping("/clear")
    public ModelAndView clear(HttpSession session) {
        Integer i = (Integer) session.getAttribute("count");
        if (i != null)
            i = -1;
        i++;
        session.setAttribute("count1", i);
        ModelAndView mav = new ModelAndView("showMember");
        return mav;
    }
    @RequestMapping("/check")
    public ModelAndView check(HttpSession session) {
        Integer i = (Integer) session.getAttribute("count");
        if (i == null)
            i = 0;
        i++;
        session.setAttribute("count", i);
        ModelAndView mav = new ModelAndView("showMember");
        return mav;
    }

    @RequestMapping("getMember")
    public ModelAndView add(Member member){
        ModelAndView mav=new ModelAndView("showMember");
        return mav;
    }

    @RequestMapping("admin_member_list")
    public String list(Model model) {
        List<Member> cs = memberService.list();
        model.addAttribute("cs1", cs);
        return "admin/listMember";
    }

    @ResponseBody
    @RequestMapping(produces="text/html;charset=UTF-8", value = "/getManyMembers")
    public String getManyMembers(Model model) {
        List<Member> cs = memberService.list();
        model.addAttribute("cs1", cs);
        System.out.println("test---------------"+JSONObject.toJSON(cs).toString());
        return JSONObject.toJSON(cs).toString();

    }

    @ResponseBody
    @RequestMapping(produces="text/html;charset=UTF-8", value = "/getOneMember")
    public String getOneMember(Model model,Member member) {
        List<Member> cs = memberService.list1(member.getMember_name() , member.getMember_password());
        model.addAttribute("cs", cs);
        System.out.println(JSONObject.toJSON(cs).toString());
        return JSONObject.toJSON(cs).toString();
    }

    @ResponseBody
    @RequestMapping("regist2")
    public String regist2(Member member) {
        String regFlag=null;
        String regMsg=null;
        System.out.println("用户注册：" + member.getMember_name() + member.getMember_password());
        String memberName = member.getMember_name();
        String memberPassword = member.getMember_password();
        if (memberName != null && memberName.trim().equals("") || memberPassword != null && memberPassword.trim().equals("")) {
            regMsg= "用户名或密码为空";
            regFlag= "isnull";
        } else {
            int check = 0;
            try {
                check = memberService.findMemberByName(memberName);
                System.out.print("__________" + check);
            } catch (Exception e) {

            }
            if (check==0) {
                regMsg= memberName;
                memberService.add(member);
                System.out.println(member.toString());
                if (memberService.findMemberByNameAndPwd(memberName,memberPassword) > 0)
                    regFlag= "success";
            }
            else {
                regFlag= "failed";
                regMsg= memberName;
            }
        }
        Map<String,String> map= new HashMap<String,String >();
        map.put("regMsg",regMsg);
        map.put("regFlag",regFlag);
        System.out.println(JSONObject.toJSON(map).toString());
        return  JSONObject.toJSON(map).toString();
    }


    @RequestMapping("regist")
    public String regist(Member member,HttpServletRequest request) {
        String memberName = request.getParameter("memberName");
        int num = memberService.findMemberByName(memberName);
        if (num > 0) {
            return "regfailed";
        } else {
            //合法用户
            memberService.add(member);
            HttpSession session = request.getSession();
            session.setAttribute("memberName", memberName);
            //return "success";
            return "regsuccess";
        }
    }
    @RequestMapping("login")
    public String memberLogin2(HttpServletRequest request) {
        String memberName = request.getParameter("memberName");
        String memberPassword = request.getParameter("memberPassword");
        int num = memberService.findMemberByNameAndPwd(memberName,memberPassword);
        if(num>0){
            //合法用户
            HttpSession session = request.getSession();
            session.setAttribute("memberName",memberName);
            session.setAttribute("memberPassword",memberPassword);
            return "index";
        }else {
            return "login2";
        }
    }

//    @ResponseBody
//    @RequestMapping("login2")
//    public String memberLogin(HttpServletRequest request) {
//        String memberName = request.getParameter("memberName");
//        String memberPassword = request.getParameter("memberPassword");
//        int num = memberService.findMemberByNameAndPwd(memberName,memberPassword);
//        if(num>0){
//            //合法用户
//            HttpSession session = request.getSession();
//            session.setAttribute("memberName",memberName);
//            session.setAttribute("memberPassword",memberPassword);
////            Map<String,String> map= new HashMap<String,String >();
////            map.put("regMsg",regMsg);
////            map.put("regFlag",regFlag);
////            System.out.println(JSONObject.toJSON(map).toString());
////            return  JSONObject.toJSON(map).toString();
////            return JSONObject.toJSON(map).toString();
//        }else {
//            return "login2";
//        }
//    }
    @RequestMapping("memberNameValidate")
    public void memberNameValidate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.读取浏览器发送过来的新增用户名
        String memberName=request.getParameter("memberName");
        //2.调用Service层进行验证
        int num =memberService.validateMemberName(memberName);
        //3.将判断结果推送到浏览器上
        response.setCharacterEncoding("utf-8");
        PrintWriter out =response.getWriter();
        if(num==0){
            //新增用户名在数据库不存在，可以使用
            out.write("contiune");
        }else {
            //新增用户名在数据库存在，不可以使用
            out.write("exsit");
        }
    }
}
