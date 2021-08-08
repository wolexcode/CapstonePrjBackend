package com.meritamerica.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.meritamerica.main.repositories.MyUserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService{
	@Autowired
	MyUserRepo userRepo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users aUser = userRepo.findByUserName(username);
        MyUserDetail userDetail = new MyUserDetail(aUser);
        System.out.println("loadUserByUsername in detail service " + userDetail.getUsername() + " password " + userDetail.getPassword());
         
        return userDetail;
	}
	
	// Converts user to spring.springframework.security.core.userdetails.User
//    private User buildUserForAuthentication(Users user,
//        List<GrantedAuthority> authorities) {
//        return new User(user.getUsername(), user.getPassword(),
//            user.isEnabled(), true, true, true, user.getAuthorityList());
//    }

//    private List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {
//
//        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();
//
//        // add user's authorities
//        for (UserRole userRole : userRoles) {
//            setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
//        }
//
//        List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
//
//        return Result;
//    }
	
}
