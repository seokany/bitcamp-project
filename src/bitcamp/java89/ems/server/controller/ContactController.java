package bitcamp.java89.ems.server.controller;



import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import bitcamp.java89.ems.server.vo.Contact;



public class ContactController {
	private Scanner in;
	private PrintStream out;
	private String filename = "contact-v1.6.data";
	private ArrayList<Contact> list;
	private boolean changed;
	//private Scanner keyScan;

	public ContactController(Scanner in, PrintStream out) {
		list = new ArrayList<Contact>();
		this.in = in;
    this.out = out;

		this.load(); // 기존의 데이터 파일을 읽어서 ArrayList를 로드한다.
	}

	public boolean isChanged() {
		return changed;
	}

	@SuppressWarnings("unchecked")
	private void load() {
		FileInputStream in0 = null;
		ObjectInputStream in = null;

		try {
			in0 = new FileInputStream(this.filename);
			in = new ObjectInputStream(in0);

			list = (ArrayList<Contact>) in.readObject();

		} catch (EOFException e) {
			// 파일을 모두 읽었다.
		} catch (Exception e) {
			System.out.println("학생 데이터 로딩 중 오류 발생!");
		} finally {
			try {
				in.close();
				in0.close();
			} catch (Exception e) {}
			}
		}
	

	public void save() throws Exception {
		FileOutputStream out0 = new FileOutputStream(this.filename);
		ObjectOutputStream out = new ObjectOutputStream(out0);

		out.writeObject(list);
		changed = false;

		out.close();
		out0.close();
	}

	public boolean service() {
		 while (true) {
			out.print("연락처관리>");
			out.println();
			String[] commands = in.nextLine().split("\\?");

			try {
				switch (commands[0]) {
				case "add" : this.doAdd(commands[1]); break;
				case "list":this.doList(); break;
				case "view":this.doView(commands[1]); break;
				case "delete": this.doDelete(commands[1]); break;
				case "update" : this.doUpdate(commands[1]); break;
				case "main": return true;
				case "quit": return false;
				default:
					System.out.println("지원하지 않는 명령어입니다.");
				}
			} catch (IndexOutOfBoundsException e) {
				out.println("인덱스가 유효하지 않습니다.");
			} catch (Exception e) {
				out.println("실행 중 오류가 발생했습니다.");
				e.printStackTrace();
			} // try
		} // while
	}

	private void doList() {
		for (Contact contact : list) {
			System.out.printf("%s,%s,%s,%s\n", contact.getName(), contact.getPosition(), contact.getTel(),
					contact.getEmail());

		}
	}

	
	 private void doUpdate(String params) {
	    String[] values = params.split("&");
	    HashMap<String,String> paramMap = new HashMap<>();
	    
	    for (String value : values) {
	      String[] kv = value.split("=");
	      paramMap.put(kv[0], kv[1]);
	    }
	    for (Contact contact : list) {
	      if (contact.getEmail().equals(paramMap.get("email"))) {
	        contact.setName(paramMap.get("name"));
	        contact.setPosition(paramMap.get("position"));
	        contact.setTel(paramMap.get("tel"));
	        changed = true;
	        out.println("변경 하였습니다.");
	        return;
	      } 
	    }
	    out.println("이메일을 찾지 못했습니다.");
	  }
	  
	 
	
	// 클라이언트에서 보낸 데이터 형식
	// => add?name=홍길동&position=대리%tel=111-1111&email=hong@test.com
	// => doAdd("name=홍길동&position=대리%tel=111-1111&email=hong@test.com")
	private void doAdd(String params) {
			String[] values = params.split("&");
			HashMap<String,String> paramMap = new HashMap<>();
			
			for (String value : values) {
				String[] kv = value.split("=");
				paramMap.put(kv[0], kv[1]);
			}
			
			
			Contact contact = new Contact();
			contact.setName(paramMap.get("name"));
			contact.setPosition(paramMap.get("position"));
			contact.setTel(paramMap.get("tel"));
			contact.setEmail(paramMap.get("email"));

			if (existEmail(contact.getEmail())) { 
				out.println("같은 이메일이 존재합니다. 등록을 취소합니다.");
				return;
			}
			
			list.add(contact);
			changed = true;
			out.println("등록하였습니다.");
		}

	private boolean existEmail(String email) {
		for (Contact contact : list) {
			if (contact.getEmail().toLowerCase().equals(email.toLowerCase())) {
				return true;
				
			}
		}
		return false;
	}

	private void doView(String params) {
		
		String[] kv = params.split("=");

		for (Contact contact : list) {
			if (contact.getName().equals(kv[1])) {
				out.println("-----------------");
				out.printf("이름: %s\n", contact.getName());
				out.printf("직위: %s\n", contact.getPosition());
				out.printf("전화: %s\n", contact.getTel());
				out.printf("이메일: %s\n", contact.getEmail());
			}
		}
	}

	private void doDelete(String params) { // 마지막 방법
		
		String[] kv = params.split("=");
		
		for (int i = 0; i < list.size(); i++) {
			Contact contact = list.get(i);
			if (contact.getEmail().equals(kv[1])) {
				list.remove(i);
				out.println("해당 데이터 삭제 완료하였습니다.");
				changed =  true;
			  break;
			}
		}
	}
		
//		private ArrayList<Contact> searchByName(String name) {
//			ArrayList<Contact> searchlist = new ArrayList<>();
//			
//			for (Contact contact : list) {
//				if (contact.getName().toLowerCase().equals(name.toLowerCase())) {
//					searchlist.add(contact);
//				}
//			}
//			return searchlist;
//		}
		
//	private void doDelete() { // 방법1
//		System.out.print("이름?");
//		String name = keyScan.nextLine();
//
//		for (int i = list.size() - 1; i >= 0; i--) {
//			if (list.get(i).getName().equals(name)) {
//				list.remove(i);
//			}
//		}
//
//		changed = true;
//		System.out.println("삭제하였습니다.");
	}
	// private void doDelete() { //방법 2
	// System.out.print("이름?");
	// String name = keyScan.nextLine();
	//
	// ArrayList<Contact> deleteList = new ArrayList<>();
	//
	// for(Contact contact : list) {
	// if (contact.getName().equals(name)) {
	// deleteList.add(contact);
	// }
	// }
	//
	// for (Contact contact : deleteList) {
	// list.remove(contact);
	// }
	// changed = true;
	// System.out.println("삭제하였습니다.");
	// }


