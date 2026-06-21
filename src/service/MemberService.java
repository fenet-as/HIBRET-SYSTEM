package service;

import model.Member;
import java.util.List;

public interface MemberService {
  
  /**
   * Add a new member to the system
   */
  void addMember(Member member);
  
  /**
   * Update an existing member
   */
  void updateMember(Member member);
  
  /**
   * Delete a member
   */
  void deleteMember(int memberId);
  
  /**
   * Get member by ID
   */
  Member getMemberById(int memberId);
  
  /**
   * Get all members
   */
  List<Member> getAllMembers();
  
  /**
   * Search members by name
   */
  List<Member> searchMembers(String searchTerm);
  
  /**
   * Get members by status
   */
  List<Member> getMembersByStatus(String status);
  
  /**
   * Get total member count
   */
  int getTotalMemberCount();
  
  /**
   * Check if email exists
   */
  boolean emailExists(String email);
  
  /**
   * Update member status
   */
  void updateMemberStatus(int memberId, String status);
}
