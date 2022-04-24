package com.gui.service;

import com.gui.domain.Group;
import com.gui.domain.Message;
import com.gui.domain.User;

import com.gui.events.EventType;
import com.gui.observer.Observable;
import com.gui.observer.Observer;
import com.gui.repository.database.GroupDbRepository;
import com.gui.repository.database.MessageDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.repository.paging.Page;
import com.gui.repository.paging.Pageable;
import com.gui.repository.paging.PageableImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.xml.sax.helpers.LocatorImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class MessageService implements Observable<EventType> {
    private MessageDbRepository repo;
    private UserDbRepository repoUser;
    private GroupDbRepository repoGroup;

    public MessageService(MessageDbRepository repo, UserDbRepository repoUser, GroupDbRepository repoGroup) {
        this.repo = repo;
        this.repoUser = repoUser;
        this.repoGroup = repoGroup;
    }


    public boolean createGroup(ArrayList<Long> users,String name) {
        for (Long e : users) {
            if (repoUser.findOne(e) == null)
                return false;
        }
        repoGroup.save(new Group(users,name));
        return true;
    }

    public Group findGroup(Long id)
    {
        return repoGroup.findOne(id);
    }

    /**
     * @param id1
     * @param message
     * @return a Message if a new message, null- otherwise
     */
    private Message createMessage(Long id1, Long group, String message) {
        User us1 = repoUser.findOne(id1);
        if (us1 == null) {
            return null;
        }

        if(!repoGroup.exists(group))
            return null;
        Message msg = new Message(us1, group, message);
        return msg;
    }

    /**
     * @param id1

     * @param message
     * @return a Message if a new message is saved, null- otherwise
     */
    public Message newMessage(Long id1, Long group, String message, Message msgg) {

        if (!repoGroup.verifyGroup(id1, group))
            return null;
        Message msg = createMessage(id1, group, message);
        if (msgg != null) {
//            System.out.println("pica1");
            msg.setReply(msgg);
        }
        if (msg == null) {
//            System.out.println("pica2");
            return null;
        }
        Message test = repo.save(msg);
        notifyObservers(new EventType());
        if (test == null) {
//            System.out.println("pica3");
            return msg;
        } else {
//            System.out.println("pica4");
            return test;
        }

    }


    /**
     * @return a Message if a new reply is saved, null- otherwise
     */
    public Message reply(Long id, Long usr, String message) {
        Message msg = repo.findOne(id);
        if (msg == null) return null;
        if (!repoGroup.verifyGroup(usr, msg.getToGroup())) {
            return new Message(null, null, null);

        }
        List<Long> ll = new ArrayList<>();
        if (msg.getFrom() == null)
            return null;
        Message reply = newMessage(usr, msg.getToGroup(), message, msg);
        if (reply == null) return null;
//        Message test = repo.update(msg);
//        if (test == null) return msg;
        else return msg;
    }


    public List<Message> showMessages(Long id) {
        return repo.findMsg(id);
    }


    public List<String> getMessages(Long id1, Long id2) {
        if (id1 == id2)
            return null;
        return repo.getConversation(id1, id2);
    }

    public void raportFriends(Long id,FriendshipService frService,LocalDate date1,LocalDate date2,File location){
        var friends=frService.raportFriends(id,date1,date2);
        ArrayList<Message> messages=raportMessages(id,date1,date2);
        PDDocument raport= new PDDocument();
        PDPage page=new PDPage();
        try {
            PDPageContentStream stream=new PDPageContentStream(raport,page);

            stream.beginText();
            stream.setLeading(14.5f);
            stream.setFont(PDType1Font.TIMES_ROMAN, 12);
            stream.newLineAtOffset(25, 725);
            String text;
            text="The friends made in the "+date1+" - "+date2+ "period are:";
            stream.showText(text);
            stream.newLine();
            for (User friend : friends) {
                text=friend.getFullName();
                stream.showText(text);
                stream.newLine();
            }
            stream.newLine();stream.newLine();
            text="The messages received in the "+date1+" - "+date2+" period are:";
            stream.showText(text);
            stream.newLine();
            for(Message msg:messages){
                text=msg.getFrom().getFullName()+" at "+msg.getDate().toString().replace("T"," ")+" in the "+repoGroup.findOne(msg.getToGroup()).getName() +" Group Chat:";
                stream.showText(text);
                stream.newLine();
                stream.showText("\""+msg.getMessage()+"\"");
                stream.newLine();
                stream.newLine();
            }
            stream.endText();
            stream.close();
            raport.addPage(page);
            raport.save(new File(location.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void raport2(Long id,Long idFriend,LocalDate date1,LocalDate date2,File location){
        ArrayList<Message> messages=raport2Messages(id,idFriend,date1,date2);
        PDDocument raport= new PDDocument();
        PDPage page=new PDPage();
        try {
            PDPageContentStream stream=new PDPageContentStream(raport,page);
            stream.beginText();
            stream.setLeading(14.5f);
            stream.setFont(PDType1Font.TIMES_ROMAN, 12);
            stream.newLineAtOffset(25, 725);
            String text;
            text="The messages received in the " +date1+" - "+date2+" period are: "+ date1+" - "+date2+ " are:";
            stream.newLine();
            for (Message msg : messages) {
                text=msg.getFrom().getFullName()+" at "+msg.getDate().toString().replace("T"," ")+" in the "+repoGroup.findOne(msg.getToGroup()).getName() +" Group Chat:";
                stream.showText(text);
                stream.newLine();
                stream.showText("\""+msg.getMessage()+"\"");
                stream.newLine();
                stream.newLine();
            }
            stream.showText(text);
            stream.endText();
            raport.addPage(page);
            raport.save(location);
            raport.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Message> raport2Messages(Long id, Long idFriend, LocalDate date1, LocalDate date2) {

        var messages = repo.findMessagesFromFriend(id,idFriend);
        ArrayList<Message> messagesRaport=new ArrayList<>();
        for (Message e : messages) {
            if(date1.compareTo(e.getDate().toLocalDate())<=0 && date2.compareTo(e.getDate().toLocalDate())>=0)
                messagesRaport.add(e);
        }
        return messagesRaport;
    }


    private ArrayList<Message> raportMessages(Long id, LocalDate date1, LocalDate date2) {
            var messages = repo.findMessagesToUser(id);
            ArrayList<Message> messagesRaport=new ArrayList<>();
            for (Message e : messages) {
                if(date1.compareTo(e.getDate().toLocalDate())<=0 && date2.compareTo(e.getDate().toLocalDate())>=0)
                    messagesRaport.add(e);
            }
            return messagesRaport;
    }


    public List<Message> getMessages2(Long id1, Long id2) {
        if(!repoGroup.verifyGroup(id1,id2))
            return null;
        return repo.getConversation2(id2);
    }

    public ArrayList<User> showMOfGroup(Long idu) {
            ArrayList<Long> userId = repoGroup.showMOfGroup(idu);
            ArrayList<User> users = new ArrayList<>();
            userId.forEach(e -> {
                users.add(repoUser.findOne(e));
            });
            return users;
    }

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Set<Long> getNextMessages(Long id) {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getMessagesOnPage(id,this.page);
    }

    //    public List<Long> showGroup(Long id) {
//        return repoGroup.getGroupsUser(id);
//    }

    public int nrOfConversations(Long id){
        int n=0;
        for(var i:repoGroup.getGroupsUser(id))
            n++;
        return n;
    }

    public List<Group> getGroups(Long id)
    {
        List<Group> list=new ArrayList<>();
        repoGroup.getGroupsUser(id).forEach(
                e->{
                    Group gr=repoGroup.findOne(e);
                    gr.setId(e);
                    list.add(gr);
                }
        );
        return list;
    }

    public Set<Long> getMessagesOnPage(Long id,int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Long> studentPage = repoGroup.getGroupsUser(id,pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }

    public Group getGroup(Long id){
        return repoGroup.findOne(id);
    }

    private List<Observer<EventType>> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer<EventType> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<EventType> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(EventType t) {
        observers.stream().forEach(x->x.update(t));
    }

    public String raportFriendsText(Long id, FriendshipService frService, LocalDate date1, LocalDate date2) {
        var friends=frService.raportFriends(id,date1,date2);
        ArrayList<Message> messages=raportMessages(id,date1,date2);
            String text="";
            text+="The friends made in the "+date1+" - "+date2+ "period are:";
            for (User friend : friends) {
                text+=friend.getFullName()+"\n";
            }
            text+="\nThe messages received in the " +date1+" - "+date2+" period are: "+ date1+" - "+date2+ " are:\n";
            for(Message msg:messages){
                text+=msg.getFrom().getFullName()+" at "+msg.getDate().toString().replace("T"," ")+"in the "+repoGroup.findOne(msg.getToGroup()).getName() +" Group Chat: \n"+msg.getMessage()+"\n";
            }
            return text;

    }

    public String raport2Text(Long id, Long idFriend, LocalDate date1, LocalDate date2) {
        ArrayList<Message> messages=raport2Messages(id,idFriend,date1,date2);
        String text="\nThe messages received in the " +date1+" - "+date2+" period are: "+ date1+" - "+date2+ " are:\n";
        for (Message msg : messages) {
            text+=msg.getFrom().getFullName()+" at "+msg.getDate().toString().replace("T"," ")+"in the "+repoGroup.findOne(msg.getToGroup()).getName() +" Group Chat: \n"+msg.getMessage()+"\n";
        }
        return text;
    }
}