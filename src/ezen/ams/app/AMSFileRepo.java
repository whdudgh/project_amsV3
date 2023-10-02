package ezen.ams.app;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import ezen.ams.domain.Account;
import ezen.ams.domain.AccountRepository;
import ezen.ams.domain.FileAccountRepository;
import ezen.ams.domain.MinusAccount;
import ezen.ams.exception.NotBalanceException;

public class AMSFileRepo {

	public static FileAccountRepository repository = new FileAccountRepository();
	private static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
		System.out.println("*******************************************************");
		System.out.println("********* " + Account.BANK_NAME + "은행 계좌 관리 애플리케이션 *********");
		System.out.println("*******************************************************");

		boolean run = true;
//		repository.addAccount(new Account("김기정", 1234, 9000));
//		repository.addAccount(new Account("박기정", 1234, 5000));
//		repository.addAccount(new Account("주기정", 1234, 12000));
//		repository.addAccount(new Account("주기정", 1234, 7000));
//		repository.addAccount(new MinusAccount("최기정", 1234, 0, 90000));
//		repository.addAccount(new MinusAccount("이기정", 1234, 0, 50000));
//		repository.addAccount(new Account("오기정", 1234, 200000));
		System.out.println(repository.maxAccountNum());
		while (run) {
			System.out.println("-------------------------------------------------------");
			System.out.println("1.계좌생성|2.계좌목록|3.입금(상환)|4.출금(대출)|5.종료");
			System.out.println("-------------------------------------------------------");
			System.out.print("선택> ");

			int selectNo = Integer.parseInt(scanner.nextLine());
			if (selectNo == 1) {
				// 계좌 생성 및 등록
				selectAccountType();
			} else if (selectNo == 2) {
				// 계좌목록
				showAccont();
			} else if (selectNo == 3) {
				// 입금
				depoMoney();
			} else if (selectNo == 4) {
				// 출금
				withdrawMoney();
			} else if (selectNo == 5) {
				run = false;
			}
		}
		repository.close();
		scanner.close();
		System.out.println("계좌관리 애플리케이션을 종료합니다.");
	}

	/**
	 * 어떤 계좌로 생성할지 선택하는 메서드(계좌 생성시 호출메서드) (2023년 5월 13일 추가함) 따로 나누지 않아도 됨.
	 * 최종 수정일 : 2023-05-23
	 * 수정 내용 : createMinus메서드를 createAccount메서드에 통합시킴(반복코드 제거)
	 */
	private static void selectAccountType() {
		System.out.println("-------------------------------------------------------");
		System.out.println("어떤 계좌를 생성 하시겠 습니까?");
		System.out.println("1.입출금계좌\t" + "2.마이너스계좌\t" + "3.뒤로가기");
		System.out.println("-------------------------------------------------------");
		System.out.print("선택> ");
		int menu = Integer.parseInt(scanner.nextLine());
		switch (menu) {
		case 1:
			createAccount(true); // 일반계좌 생성 메서드
			break;
		case 2:
			createAccount(false); // 마이너스계좌 생성 메서드
			break;
		case 3:
			break;
		}
	}
	
	/**
	 * 키보드(표준입력)로부터 계좌정보 입력 받아 계좌 생성하기(selectAccountType()에서 호출됨.)
	 * 최종 수정일 : 2023-05-23
	 * 수정 내용 : createAccount메서드와 createMinusAccount매서드를 통합하고 후자를 지움. 
	 */
	private static void createAccount(boolean type) {
		int maxAccNum = repository.maxAccountNum();
//		System.out.println(accNum);
		System.out.print("예금주명: ");
		String owner = scanner.nextLine();
		
		System.out.print("비밀번호: ");
		int passwd = Integer.parseInt(scanner.nextLine());

		if (type) {
			
			System.out.print("입금액: ");
			long inputMoney = Long.parseLong(scanner.nextLine());
			Account account = new Account(owner, passwd, inputMoney);
			
			// AccountRepository에 계좌등록
			repository.addAccount(account);
			System.out.println("※ 계좌 정상 등록 처리되었습니다.");
		} else {
			int firstMoney = 0;

			System.out.print("대출액: ");
			long borrowMoney = Long.parseLong(scanner.nextLine());

			Account account = new MinusAccount(owner, passwd, firstMoney, borrowMoney);

			// AccountRepository에 마이너스계좌등록
			repository.addAccount(account);
			System.out.println("※ 계좌 정상 등록 처리되었습니다.");
		}
	}

	/**
	 * 계좌 목록 보여주기 (2023년 5월 18일 수정함)
	 */
	private static void showAccont() {
		lineForCustomer();
		List<Account> list = repository.getAccounts();
		for (Account account : list) {
			if (account instanceof MinusAccount) {
				System.out.println("마이너스" + account);
			} else if (account instanceof Account) {
				System.out.println(" 입출금\t" + account);
			}
		}
	}

	/**
	 * 입금기능 
	 * 최종 수정일 : 2023 05 19 
	 * 수정 내용 : 계좌 정보 보여주는 input메서드 없애고 해당 코드를 if문 안에 각각 넣음.
	 */
	private static void depoMoney() {
		System.out.print("입금또는 상환할 계좌번호를 입력하시오. => ");

		String accountNum = scanner.nextLine();
		Account searchAccount = repository.searchAccount(accountNum);

		// searchAccount는 Account와 MinusAccount 모두 자식으로 포함이라 작은단위부터 비교
		if (searchAccount instanceof MinusAccount) {
			lineForCustomer();
			System.out.println("마이너스     " + searchAccount.toString());
			MinusAccount min_searchAccount = (MinusAccount) repository.searchAccount(accountNum);
			try {
				System.out.print("해당 계좌가 맞으시면 상환할 금액을 입력 하시오 => ");
				long money = Long.parseLong(scanner.nextLine());
				min_searchAccount.deposit(money);
				System.out.println(money + "(만)원을 상환 하셨습니다.");
				System.out.println("현재 남은 상환액은 " + min_searchAccount.getBorrowMoney() + "(만)원 입니다.");
			} catch (NotBalanceException no) {
				errMessage(no);
			}
		} else if (searchAccount instanceof Account) {
			lineForCustomer();
			System.out.println(" 입출금      " + searchAccount.toString());

			try {
				System.out.print("해당 계좌가 맞으시면 입금할 금액을 입력 하시오 => ");
				long money = Long.parseLong(scanner.nextLine());
				searchAccount.deposit(money);
				System.out.println(money + "(만)원을 입금 하셨습니다.");
				System.out.println("현재 잔액은" + searchAccount.getRestMoney() + "(만)원 입니다.");
			} catch (NotBalanceException no) {
				errMessage(no);
			}
		} else {
			System.out.println("계좌번호에 해당하는 계좌가 존재하지 않습니다..");
		}
	}

	/**
	 * 출금기능 최종 수정일 : 2023 05 19 수정 내용 : 계좌 정보 보여주는 input메서드 없애고 해당 코드를 if문 안에 각각 넣음.
	 */
	private static void withdrawMoney() {
		System.out.print("출금이나 대출을 원하시는 계좌번호를 입력하시오. => ");

		String accountNum = scanner.nextLine();
		Account searchAccount = repository.searchAccount(accountNum);

		if (searchAccount instanceof MinusAccount) {
			lineForCustomer();
			System.out.println("마이너스     " + searchAccount.toString());
			MinusAccount minAccount = (MinusAccount) repository.searchAccount(accountNum);

			try {
				System.out.print("해당 계좌가 맞으시면 대출할 금액을 입력 하시오 => ");
				long money = Long.parseLong(scanner.nextLine());
				minAccount.withdraw(money);
				System.out.println(money + "(만)원을 대출 하셨습니다.");
				System.out.println("현재 대출액은" + minAccount.getBorrowMoney() + "(만)원 입니다.");
			} catch (NotBalanceException no) {
				errMessage(no);
			}

		} else if (searchAccount instanceof Account) {
			lineForCustomer();
			System.out.println(" 입출금      " + searchAccount.toString());

			try {
				System.out.print("해당 계좌가 맞으시면 출금할 금액을 입력 하시오 => ");
				long money = Long.parseLong(scanner.nextLine());
				searchAccount.withdraw(money);
				System.out.println(money + "(만)원을 출금 하셨습니다.");
				System.out.println("현재 잔액은" + searchAccount.getRestMoney() + "(만)원 입니다.");
			} catch (NotBalanceException no) {
				errMessage(no);
			}
		} else {
			System.out.println("계좌번호에 해당하는 계좌가 존재하지 않습니다..");
		}
	}

	/**
	 * 목록에서 해당 요소별 카테고리 라인(데코레이션용) (2023년 5월 13일 추가함) 개별사용 없음 showAccont() 와 입출금 기능인
	 * withdrawMoney(),depoMoney() 에서 호출됨.
	 */
	private static void lineForCustomer() {
		System.out.println("-------------------------------------------------------");
		System.out.println("계좌타입\t" + "계좌\t" + " 성함\t" + "비밀번호  잔액" + "\t" + "대출액");
		System.out.println("-------------------------------------------------------");
	}

	/**
	 * 에러메세지 출력용 메서드(2023년 05월 17일 추가함)
	 * 
	 * @param 에러메세지
	 */
	private static void errMessage(NotBalanceException no) {
		System.err.println(no.getMessage());
		System.err.println("메뉴 화면으로 돌아 갑니다.");
	}
}
