package com.gui.repository.paging;


import com.gui.domain.Entity;
import com.gui.domain.Event;
import com.gui.domain.User;
import com.gui.repository.RepoPag;

public interface PagingRepository<ID , E extends Entity<ID>> extends RepoPag<ID, E> {

    Page<Long> getGroupsUser(ID id,Pageable pageable);
    Page<User> getUsers(Pageable pageable);
    Page<User> getAllFriends(ID id,Pageable pageable,String st);
    Page<Event> getAllEvents(Pageable pageable);

}
