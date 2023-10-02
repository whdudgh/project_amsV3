package ezen.ams.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 메모리(리스트)를 이용한 은행계좌 목록 저장 및 관리(검색, 수정, 삭제) 구현체
 * 
 * @author 조영호
 * @author 조영호
 * @since 1.0
 */
public class ListAccountRepository implements AccountRepository{

	public List<Account> accounts;

	public ListAccountRepository() {
		this.accounts = new LinkedList();
	}

	/**
	 * 전체계좌 목록 수 반환
	 * 
	 * @return 목록수
	 */
	public int getCount() {
		return accounts.size();
	}

	/**
	 * 
	 * @return 전체계좌 목록
	 */
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * 신규계좌 등록
	 * 
	 * @param account 신규계좌
	 * @return 성공여부(등록여부)
	 */
	public boolean addAccount(Account account) {
//		데이터 검증이 필요하지만 편의상 생략
		accounts.add(account);
		return true;
	}

	/**
	 * 계좌로 사용자 조회
	 * 
	 * @param accountNum 검색 계좌번호
	 * @return 검색된 계좌
	 */
	public Account searchAccount(String accountNum) {
		for (Account account : accounts) {
			if(account.getAccountNum().equals(accountNum)) {
				return account;
			}
		}
		return null;
	}

	/**
	 * 예금주명으로 계좌조회
	 * 
	 * @param accountOwner 검색 예금주명
	 * @return 검색된 계좌목록
	 */
	public List<Account> searchAccountByOwner(String accountOwner) {
		List<Account> searchAccounts = new ArrayList<>();
		for (Account account : accounts) {
			if(account.getAccountOwner().equals(accountOwner)) {
				searchAccounts.add(account);
			}
		}	
		return searchAccounts;
	}

	/**
	 * 입력된 계좌번호 삭제
	 *  
	 * @param accountNum 삭제할 계좌번호
	 * @return 삭제여부
	 */
	public boolean removeAccount(String accountNum) {
		for (int i = 0; i < accounts.size(); i++) {
			Account account = accounts.get(i);
			if(account.getAccountNum().equals(accountNum)) {
				accounts.remove(i);
				return true;
			}
		}
		return false;
	}
}