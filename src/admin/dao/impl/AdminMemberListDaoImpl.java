package admin.dao.impl;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import admin.dao.face.AdminMemberListDao;
import user.member.dto.MemberDTO;
import util.JDBCTemplate;

public class AdminMemberListDaoImpl implements AdminMemberListDao{
	 	private Connection conn =null;
	    private PreparedStatement ps =null;
	    private ResultSet rs=null;
	     
	    //sql StringBuffer
	    //String하면 안됨, opt조건에 따라서 append 함수로 where문 추가
    	private StringBuffer sql;
	    //인스탠스
	    private static AdminMemberListDaoImpl instance;
	    
	    //접근을 막는 private 생성자
	    private AdminMemberListDaoImpl(){}
	    
	    //null값일때 instance 생성 가능 메소드
	    public static AdminMemberListDaoImpl getInstance(){
	        if(instance==null)
	            instance=new AdminMemberListDaoImpl();
	        return instance;
	    }
	    
	   /**
	    * 회원 조회 - 검색, 필터, 정렬에 따라( 전체,선택 조회)
	    * 
	    */
	    public List<MemberDTO> select(HashMap <String,Object> listOpt){	  
	    	
	    	conn= JDBCTemplate.getConnection();
	    	
	    	String grade = (String)listOpt.get("grade"); //검색옵션, 회원등급
	    	String opt = (String)listOpt.get("opt"); // 검색옵션(아이디,이름, 휴대폰번호등..)
	        String condition = (String)listOpt.get("condition"); // 검색내용
	        
	        //sql 객체 새로생성
	        sql=new StringBuffer();
	    	
	    	//반환 객체 List 생성
	    	List <MemberDTO> list = new ArrayList<>();
	    	
	    	sql.append("select * from member");          
            String search="";
            try {      
	            //grade를 선택안했을 때 where opt만 붙여준다 
		    	if(grade.equals("5")) {
			    	//회원 전체 조회(검색 디폴트값)
			    	if(opt.equals("0")) {		    			    		
			    			    		
			    	}
			    	// 정렬조건 - 아이디로 검색
			    	else if(opt.equals("1")) {
			    		search=" where userId=%"+condition+"%";	
			    	}
			    	//정렬조건 - 이름으로 검색
			    	else if(opt.equals("2")) {
			    		search=" where userName=%"+condition+"%";		    		
			    	}
		    	}
		    	//grade가 null이 아닐때
		    	else {
			    	//회원 전체 조회(검색 디폴트값)
		    		if(opt.equals("0")) {
		    			search=" where userGrade=%"+grade+"%";				             		         
			    	}
			    	// 정렬조건 - 아이디로 검색
			    	else if(opt.equals("1")) {
			    		search=" where userGrade=%"+grade+"% and userId=%"+condition+"%";	
			    	}
			    	//정렬조건 - 이름으로 검색
			    	else if(opt.equals("2")) {
			    		search=" where userGrade=%"+grade+"% userName=%"+condition+"%";		    				    		
			    	}
		    	} 
		    	//sql문 완성(정렬조건, 검색조건 충족)	    	
		    	sql.append(search);	 
		    	sql.append(" order by userid");
		    	
		    	ps=conn.prepareStatement(sql.toString());
		    	rs=ps.executeQuery(sql.toString());
		    	
		    	System.out.println(sql.toString());
		    	System.out.println(rs.next());
		    	
		    	while(rs.next()) {
		    		//멤버객체 생성
			    	MemberDTO member =new MemberDTO();
			    	member.setUserid(rs.getString("userid"));
			    	member.setUsername(rs.getString("username"));
			    	member.setUsertel(rs.getInt("userTel"));
			    	member.setUseremail(rs.getString("userEmail"));
			    	member.setUserbirth(rs.getString("userBirth"));
			    	member.setUseraddress(rs.getString("userAddress"));	
			    	member.setUsergrade(rs.getInt("userGrade"));
			    	member.setUserregdate(rs.getDate("userRegDate"));
			    	list.add(member);
		    	}
            }catch(SQLException e) {
            	e.printStackTrace(); 	
            }finally {
            	JDBCTemplate.close(rs);
            	JDBCTemplate.close(ps);            	           	
            }
           
	    	return list;
 	    	
	    } //END select
	    
	    /**
	     * 회원 수 카운트 (검색 조건에 따라 다름)
	     */
	    @Override
	    public int getMemberCount(HashMap<String, Object> listOpt) {
	    		    		    	
	    	String grade =(String)listOpt.get("grade"); //등급
	    	String opt = (String)listOpt.get("opt"); // 검색옵션(아이디,이름, 휴대폰번호등..)
	        String condition = (String)listOpt.get("condition"); // 검색내용
	    	
	        //sql 객체 새로생성
	        sql=new StringBuffer();
	        
	        //글개수 카운트 인트값 생성
	        int cnt=0;
	        
	        sql.append("select count(*) from member");
	        String search="";
	        
	        try {
	        	conn=JDBCTemplate.getConnection();
	            //grade를 선택안했을 때 where opt만 붙여준다 
		    	if(grade.equals("5")) {
			    	//회원 전체 조회(검색 디폴트값)
		    		if(opt.equals("0")) {	
			    	  ps = conn.prepareStatement(sql.toString());	             			             			             
			          sql.delete(0, sql.toString().length());
			    	}
			    	// 정렬조건 - 아이디로 검색
			    	else if(opt.equals("1")) {
			    		search=" where userid like %"+condition+"%";			    					    		
			    	}
			    	//정렬조건 - 이름으로 검색
			    	else if(opt.equals("2")) {
			    		search=" where username like %"+condition+"%";			    					    				    					    		
			    	}
		    	}
		    	//grade가 null이 아닐때
		    	else {
			    	//회원 전체 조회(검색 디폴트값)
		    		if(opt.equals("0")) {
			    		search=" where userGrade like %"+grade+"%";	            		             			          
		    		}
			    	// 정렬조건 - 아이디로 검색
			    	else if(opt.equals("1")) {
			    		search=" where userid like %"+condition+"% and usergrade like %"+grade+"%";
			    	}
			    	//정렬조건 - 이름으로 검색
			    	else if(opt.equals("2")) {
			    		search=" where userid like %"+condition+"% and userGrade like %"+grade+"%";		    				    		
			    	}
		    	} 
		    	sql.append(search);
		    	
		    	System.out.println(sql.toString());
		    	ps = conn.prepareStatement(sql.toString());
		    	//sql 완성	-> 실행	    	
		    	rs=ps.executeQuery();
		    	
		    	//값 반환
		    	if(rs.next()) {
		    		cnt = rs.getInt(1);
		    	}
 	
	        }catch(SQLException e) {
	        	e.getStackTrace();
	        }finally {
	        	JDBCTemplate.close(rs);
	        	JDBCTemplate.close(ps);	        	
	        }	        	
	    	return cnt;
	    } //END getMemberCount
	  
	    /**
	     * 회원 삭제
	     */
	    public int delete(MemberDTO member) {
	    	//반환값 int생성
	    	int res=0;
	    	sql=new StringBuffer();
	    	try {
	    		conn=JDBCTemplate.getConnection();
	    		sql.append("delete from member");
	    		sql.append(" where uesrid=?");
	    		ps=conn.prepareStatement(sql.toString());
	    		ps.setString(1, member.getUserid());
	    		res=ps.executeUpdate();
	    		
	    	}catch(SQLException e) {
	    		e.printStackTrace();
	    	}finally{
	    		JDBCTemplate.close(ps);
	    	}
  		    	
			return res;
	    	
	    }
	    
	    
}














