package dao;

import model.Member;
import java.util.List;

public interface MemberDAO {
  
  /**
   * Add a new member
   */
  void add(Member member);
  
  /**
   * Update an existing member
   */
  void update(Member member);
  
  /**
   * Delete a member
   */
  void delete(int memberId);
  
  /**
   * Get member by ID
   */
  Member getById(int memberId);
  
  /**
   * Get all members
   */
  List<Member> getAll();
  
  /**
   * Search members by name or email
   */
  List<Member> search(String searchTerm);
  
  /**
   * Get members by status
   */
  List<Member> getByStatus(String status);
  
  /**
   * Count total members
   */
  int count();
  
  /**
   * Check if email exists
   */
  boolean emailExists(String email);
  
  /**
   * Get members in a specific group
   */
  List<Member> getMembersInGroup(int groupId);
}
