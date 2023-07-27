package kr.smhrd.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.smhrd.entity.Member;
import kr.smhrd.entity.MyLog;
import kr.smhrd.entity.Page;
import kr.smhrd.entity.Perfume;
import kr.smhrd.mapper.MemberMapper;
import kr.smhrd.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	private MemberMapper mapper;
	@Autowired
	private MemberService service;

	// 메인화면 이동
	@RequestMapping("/")
	public String goMain() {
		return "MainPage";
	}

	// 로그인 페이지로 이동
	@RequestMapping("/Login")
	public String goLogin() {
		return "LoginPage";
	}

	// Allperfume페이지 이동
	@RequestMapping("/AllP")
	public String goAllP(HttpSession session) {
		return "AllPerfume";
	}

	// 음악으로 향수 추천받기 페이지 이동
	@RequestMapping("/goMusicPerfume")
	public String goMusicPerfume(HttpSession session, Model model) {
		int loginCheck = service.loginCheck(session);
		if (loginCheck == 0) {
			return "LoginPage";
		}
		return "MusicPerfume";
	}

	// 로그페이지 이동 ( 페이지번호기능 추가 )
		@RequestMapping("/goLogPage")
		public String goLogPage(@RequestParam(required=false, defaultValue = "1")int PageNo, HttpSession session, Model model) {
			// 로그인 확인.
			int loginCheck = service.loginCheck(session);
			if (loginCheck == 0) {
				return "LoginPage";
			}
			Member loginUser = (Member) session.getAttribute("user");
			String ID = loginUser.getID();
			
			Page page = new Page( PageNo, 4, mapper.count(ID) );
			int startNo = page.getStartNo()-1;
//			int endNo = page.getEndNo();
			int totalCount = page.getTotalCount();
			int totalPage = page.getTotalPage();
			model.addAttribute("totalPage", totalPage);
			
//			System.out.println("토탈카운트 : " + totalCount);
//			System.out.println("토탈페이지 : " + totalPage);
			
//			Map<String,Integer> Map = new HashMap<>();
//			Map.put("startNo", page.getStartNo());
//			Map.put("endNo", page.getEndNo());
			ArrayList<MyLog> log = mapper.getLogList(ID, startNo);
			model.addAttribute("log", log);

			return "LogPage";
			
		}

//      int i=0;
//      ArrayList<ArrayList<Perfume>> logListPerfume = new ArrayList<ArrayList<Perfume>>();
//      for (MyLog logList : log) {
//         i++;
//         System.out.println(logList);
//         int num1 = logList.getP_NUM1();
//         int num2 = logList.getP_NUM2();
//         int num3 = logList.getP_NUM3();
//
//         ArrayList<Perfume> logPerfume = mapper.LogLoadPInfo(num1, num2, num3);
////         System.out.println(logPerfume);
////         System.out.println(logPerfume.get(0));

//         
//         logListPerfume.add(logPerfume);
//         
//         if (log.size() == i) {
//            session.setAttribute("logListPerfume", logListPerfume);
//            System.out.println(logListPerfume);
//         }      
//      MyLog b = log.get(0);
//      int P_num1 = b.getP_NUM1();


	// 향선택 향수 찾기 페이지 이동
	@RequestMapping("/ScentP")
	public String MixP(HttpSession session, Model model) {
		int loginCheck = service.loginCheck(session);
		if (loginCheck == 0) {
			return "LoginPage";
		}
		ArrayList<Perfume> Top = mapper.MixTop();
		ArrayList<Perfume> Mid = mapper.MixMid();
		ArrayList<Perfume> Base = mapper.MixBase();
		model.addAttribute("Ptop", Top);
		model.addAttribute("Pmid", Mid);
		model.addAttribute("Pbase", Base);
		return "ScentPerfume";
	}

	// 로그인
	@PostMapping("/Login")
	public String Login(Member member, HttpSession session) {
		Member user = mapper.Login(member);
		session.setAttribute("user", user);
		
		String nextView = null;
		
		if (user != null) {
			nextView = "MainPage";
		} else {
			nextView = "loginFail";
		}
			return nextView;
	}

	// 로그아웃
	@RequestMapping("/logout")
	public String Logout(HttpSession session) {
		session.removeAttribute("user");
		return "redirect:/";
	}

	// 회원가입 페이지로
	@RequestMapping("/Join")
	public String goJoin() {
		return "JoinPage";
	}

	// 회원가입
	@PostMapping("/Join")
	public String Join(Member member) {
		int row = mapper.Join(member);
		if (row > 0) {
			return "redirect:/";     // 회원가입 성공 시 메인으로 이동
		} else
			return "redirect:/Join"; // 실패

	}

}