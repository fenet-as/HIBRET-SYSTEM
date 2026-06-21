package service.impl;

import dao.MemberDAO;
import dao.impl.MemberDAOImpl;
import model.Member;
import service.MemberService;
import java.util.List;

public class MemberServiceImpl implements MemberService {
  
  private MemberDAO memberDAO;
  
  public MemberServiceImpl() {
    this.memberDAO = new MemberDAOImpl();
  }
  
  @Override
  public void addMember(Member member) {
    if (member == null) {
      throw new IllegalArgumentException("Member cannot be null");
    }
    if (member.getFirstName() == null || member.getFirstName().isEmpty()) {
      throw new IllegalArgumentException("First name is required");
    }
    if (member.getLastName() == null || member.getLastName().isEmpty()) {
      throw new IllegalArgumentException("Last name is required");
    }
    if (emailExists(member.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }
    memberDAO.add(member);
  }
  
  @Override
  public void updateMember(Member member) {
    if (member == null || member.getId() <= 0) {
      throw new IllegalArgumentException("Valid member required");
    }
    memberDAO.update(member);
  }
  
  @Override
  public void deleteMember(int memberId) {
    if (memberId <= 0) {
      throw new IllegalArgumentException("Valid member ID required");
    }
    memberDAO.delete(memberId);
  }
  
  @Override
  public Member getMemberById(int memberId) {
    if (memberId <= 0) {
      throw new IllegalArgumentException("Valid member ID required");
    }
    return memberDAO.getById(memberId);
  }
  
  @Override
  public List<Member> getAllMembers() {
    return memberDAO.getAll();
  }
  
  @Override
  public List<Member> searchMembers(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      return memberDAO.getAll();
    }
    return memberDAO.search(searchTerm);
  }
  
  @Override
  public List<Member> getMembersByStatus(String status) {
    if (status == null || status.isEmpty()) {
      return memberDAO.getAll();
    }
    return memberDAO.getByStatus(status);
  }
  
  @Override
  public int getTotalMemberCount() {
    return memberDAO.count();
  }
  
  @Override
  public boolean emailExists(String email) {
    return memberDAO.emailExists(email);
  }
  
  @Override
  public void updateMemberStatus(int memberId, String status) {
    Member member = getMemberById(memberId);
    if (member != null) {
      member.setStatus(status);
      updateMember(member);
    }
  }
}
