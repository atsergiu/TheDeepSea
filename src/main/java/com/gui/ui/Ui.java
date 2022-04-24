package com.gui.ui;

import com.gui.domain.Friendship;
import com.gui.domain.Message;
import com.gui.domain.Tuple;
import com.gui.domain.User;
import com.gui.domain.validators.ValidationException;
import com.gui.service.FriendshipService;
import com.gui.service.MessageService;
import com.gui.service.UserService;
import org.postgresql.util.PSQLException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * class for UI
 */
public class Ui {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    public Ui(UserService userService, FriendshipService friendshipService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    private void logare() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID:");
        String id = null;
        try {
            id = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User test = userService.findOne(Long.parseLong(id));
        if (test == null) {
            System.out.println("Utilizatorul cu id-ul dat nu exista");
            return;
        } else System.out.println(test);

        boolean ok = true;
        while (ok) {
            System.out.println();

            System.out.println("Scrieti numarul functionalitatii dorite:");
            System.out.println("1.Adaugare prietenie");
            System.out.println("2.Stergere prietenie");
            System.out.println("3.Afisare cereri de prietenie");
            System.out.println("4.Accepta/respinge cerere de prietenie");
            System.out.println("5.Afisare o prietenie");
            System.out.println("6.Afiseaza prietenii");
            System.out.println("7.Afisare prietenii dintr-o luna");
            System.out.println("8.Trimite un mesaj");
            System.out.println("9.Raspunde la un mesaj");
            //System.out.println("10.Afisare mesaje la care nu ai raspuns");
            System.out.println("10.Afisare mesaje dintr o conversatie cronologic");
            System.out.println("11.Creare Conversatie");
            System.out.println("12.Afisare Conversatii din care fac parte");
            System.out.println("13.Afisare membrii Conversatie");
            System.out.println("0.Deconectare");

            String choice = null;
            try {
                choice = reader.readLine();
                switch (choice) {
                    case "1":
                        addFriendship(test.getId());
                        break;
                    case "2":
                        removeFriendship(test.getId());
                        break;
                    case "3":
                        friendReq(test.getId());
                        break;
                    case "4":
                        statusFriendship(test.getId());
                        break;
                    case "5":
                        oneFriendship(test.getId());
                        break;
                    case "6":
                        showFriendsOfUser(test.getId());
                        break;
                    case "7":
                        showFriendsMonth(test.getId());
                        break;
                    case "8":
                        sendNewMessage(test.getId());
                        break;
                    case "9":
                        replyMessage(test.getId());
                        break;
                    case "WIP":
                        showMessages(test.getId());
                        break;
                    case "10":
                        showConversation(test.getId());
                        break;
                    case "11":
                        createGroup(test.getId());
                        break;
                    case "12":
                        showGroup(test.getId());
                        break;
                    case "13":
                        showMOfGroup(test.getId());
                        break;
                    case "0":
                        ok = false;
                        break;
                    default:
                        System.out.println("Comanda gresita, incearca din nou");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Id-ul trebuie sa fie un numar intreg");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void showMOfGroup(Long id) {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("ID Conversatie:");
//        try {
//            String idu = reader.readLine();
//            var users = messageService.showMOfGroup(id, idu);
//            if (users.size() != 0)
//                System.out.println("Membrii sunt:");
//            users.forEach(System.out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void showGroup(Long id) {
        System.out.println("Faci parte din urmatoarele grupuri:");
        //messageService.showGroup(id).forEach(System.out::println);
        messageService.setPageSize(2);
        System.out.println("Elements on page 1");
        messageService.getMessagesOnPage(id,0).stream().forEach(System.out::println);
        System.out.println("Elements on next page");
        //messageService.getNextMessages(id).stream().forEach(System.out::println);
        //System.out.println("Elements on page 2");
        //messageService.getMessagesOnPage(id,1).stream().forEach(System.out::println);
    }

    private void createGroup(Long id) {
//        ArrayList<Long> group = new ArrayList<>();
//        group.add(id);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Lista id utilizatori separata de virgula:");
//        String li = null;
//        try {
//            li = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String[] parts = li.split(",");
//        for (var i : parts)
//            if (group.contains(Long.parseLong(i))) {
//                System.out.println("Nu poti avea duplicate ba ce faci");
//                return;
//            } else group.add(Long.parseLong(i));
//        if(messageService.createGroup(group))
//            System.out.println("Grup creat cu succes");
//        else System.out.println("Grupul nu a fost creat");
    }

    private void showMessages(Long id) {
        messageService.showMessages(id).forEach(e ->
        {
            if (e.getFrom() == null)
                System.out.println("Mesaj de la : Deleted User");
            else
                System.out.println("Mesaj de la :" + e.getFrom());
            System.out.println("Mesajul: " + e.getMessage());
            System.out.println("Id mesaj: " + e.getId());
            System.out.println();
        });
    }

    public void run() {
        boolean ok = true;
        while (ok) {
            System.out.println();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            System.out.println("Scrieti numarul functionalitatii dorite:");
            System.out.println("1.Login");
            System.out.println("2.Adaugare utilizator");
            System.out.println("3.Stergere utilizator");
            System.out.println("4.Modificare utilizator");
            System.out.println("5.Afisare numar comunitati");
            System.out.println("6.Afisare cea mai sociabila comunitate");
            System.out.println("7.Afisare Utilizatori");
            System.out.println("8.Afisare total prietenii");
            System.out.println("0.Iesire");

            String choice = null;
            try {
                choice = reader.readLine();
                switch (choice) {
                    case "1":
                        logare();
                        break;
                    case "2":
                        addUser();
                        break;
                    case "3":
                        removeUser();
                        break;
                    case "4":
                        modifyUser();
                        break;
                    case "5":
                        communities();
                        break;
                    case "6":
                        maxCommunity();
                        break;
                    case "7":
                        showUsers();
                        break;
                    case "8":
                        showFriendships();
                        break;
                    case "0":
                        ok = false;
                        break;
                    default:
                        System.out.println("Comanda gresita, incearca din nou");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Id-ul trebuie sa fie un numar intreg");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (PSQLException throwables) {
                System.out.println("Nu poti sterge userul din baza de date");
            }
        }
    }

    private void showConversation(Long id) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID Conversatie:");
        String idu = reader.readLine();
        var msgs = messageService.getMessages2(id, Long.parseLong(idu));
        if (msgs == null)
            return;
        msgs.forEach(e -> {
            var date = e.getDate().toString().split("T");
            System.out.println("Mesaj dat la data de: " + date[0] + " " + date[1]);
            System.out.println("Id-ul mesajului: " + e.getId());
            if (e.getReply().getId() != 0)
                System.out.println("Reply la mesajul cu id " + e.getReply().getId());
            if (e.getFrom() == null)
                System.out.println("Mesaj de la Deleted User");
            else
                System.out.println("Mesaj de la " + e.getFrom().getLastName()
                        + " " + e.getFrom().getFirstName());
            System.out.println("Textul mesajului: ");
            System.out.println(e.getMessage());
            System.out.println();
        });

    }

    private void replyMessage(Long id) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID mesaj:");
        String idm = reader.readLine();
        System.out.println("Mesaj: ");
        String mesaj = reader.readLine();
        Message test = messageService.reply(Long.parseLong(idm), id, mesaj);
        if (test == null) System.out.println("Nu exista conversatia");
        else if (test.getFrom() == null)
            System.out.println("Nu poti da acest reply");
    }

    private void sendNewMessage(Long id) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Id Conversatie: ");
        String idConv = reader.readLine();
        System.out.println("Mesaj: ");
        String mesaj = reader.readLine();
        Message msg = messageService.newMessage(id, Long.parseLong(idConv), mesaj, null);
        if (msg == null) System.out.println("Conversatia nu exista\n");
        else if (msg.getFrom() == null)
            System.out.println("O conversatie deja este inceputa");
    }

    private void friendReq(Long id1) {
        friendshipService.showFriendReq(id1).forEach(System.out::println);
    }

    private void statusFriendship(Long id1) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID2:");
        String id2 = reader.readLine();
        System.out.println("Accept/Respinge");
        String status = reader.readLine();
        Friendship test = friendshipService.changeStatus(id1, Long.parseLong(id2), status);
        if (test == null)
            System.out.println("Cererea de prietenie nu exista");
        else if (test.getU1() == -1) System.out.println("Tu ai trimis cererea, nu poti schimba tot tu statusul");

    }

    private void showFriendsMonth(Long id) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        System.out.println("Luna:");
        String month = reader.readLine();

        List<User> elems = friendshipService.monthFriends(id, Integer.parseInt(month));
        if (elems.size() == 0)
            System.out.println("Utilizatorul nu are prieteni din luna aia,trist:(");
        elems.forEach(e -> {
            System.out.println(e.getLastName() + '|' + e.getFirstName() + '|' +
                    friendshipService.findOne(new Tuple<Long, Long>
                            (id, e.getId())).getDate());
        });
    }

    private void oneFriendship(Long id1) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID2:");
        String id2 = reader.readLine();
        Tuple<Long, Long> id = new Tuple<>(id1, Long.parseLong(id2));
        Friendship test = friendshipService.findOne(id);
        if (test == null)
            System.out.println("Prietenia nu exista");
        else System.out.println(test);
    }


    private void modifyUser() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("ID:");
        String id = reader.readLine();
        System.out.println("Prenume:");
        String firstName = reader.readLine();
        System.out.println("Nume:");
        String lastName = reader.readLine();
        System.out.println("Gen(Poate fi doar Masculin/Feminin/Altele): ");
        String gender = reader.readLine();
        User user = new User(firstName, lastName, gender);
        user.setId(Long.parseLong(id));
        User test = userService.modifyUser(user);
        if (test != null)
            System.out.println("Utlizatorul nu exista!\n");
    }

    private void showFriendships() {
        friendshipService.getAll().forEach(System.out::println);
    }

    private void showFriendsOfUser(Long id) throws IOException {
        List<Friendship> friends = friendshipService.getAllFriendshipOfAUser(id,"Accept");
        if (friends.size() == 0)
            System.out.println("Utilizatorul nu are prieteni");
        else {
            System.out.println("Prietenii utilizatorului sunt: ");
            friends.forEach(e -> {
                        if (e.getU1() != id) {
                            User usr = userService.findOne(e.getU1());
                            System.out.println(usr.getFirstName() + "|" + usr.getLastName() + "|" + e.getDate());
                        } else {
                            User usr = userService.findOne(e.getU2());
                            System.out.println(usr.getFirstName() + "|" + usr.getLastName() + "|" + e.getDate());
                        }
                    }
            );
        }
    }

    private void showUsers() {
        userService.getAll().forEach(System.out::println);
    }

    private void removeUser() throws IOException, PSQLException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("ID:");
        String id = reader.readLine();
        User test = userService.deleteUtilizator(Long.parseLong(id), friendshipService);
        if (test == null)
            System.out.println("Utilizatorul cu id-ul dat nu exista");

    }

    private void addUser() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Prenume:");
        String firstName = reader.readLine();
        System.out.println("Nume:");
        String lastName = reader.readLine();
        System.out.println("Gen(Poate fi doar Masculin/Feminin/Altele): ");
        String gender = reader.readLine();
        System.out.println("Email: ");
        String email=reader.readLine();
        System.out.println("Parola: ");
        String parola=reader.readLine();
        User user = new User(firstName, lastName, gender,email,parola);
        User test = userService.addUtilizator(user);
        if (test != null)
            System.out.println("Elementul deja este salvat");
    }

    private void addFriendship(Long id1) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID2:");
        String id2 = reader.readLine();
        Long aux, idl1, idl2;
        idl2 = Long.parseLong(id2);
        Friendship friend = new Friendship(id1, idl2);
        if (friendshipService.findOne(friend.getId()) != null) {
            System.out.println("Prietenia deja exista!\n");
            return;
        }
        Friendship test = friendshipService.addFriendship(friend);
        System.out.println(test);
        if (test == null)
            System.out.println("Unul din utilizatorii dati nu exista!\n");
        else if (test.getU1() != -1L)
            System.out.println("Prietenia deja exista!\n");
    }

    private void removeFriendship(Long idl1) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ID2:");
        String id2 = reader.readLine();
        Long aux, idl2;
        idl2 = Long.parseLong(id2);
        Tuple<Long, Long> id = new Tuple<>(idl1, idl2);
        var test = friendshipService.deleteFriendship(id);
        if (test == null)
            System.out.println("Prietenia nu exista, probabil nu te suporta\n");
    }

    private void communities() {
        System.out.println("Numarul de comunitati este "
                + userService.nrOfCom());
        System.out.println();
    }

    private void maxCommunity() {
        System.out.println("Comunitatea cea mai activa este: ");
        var com = userService.mostSocialCom();
        com.forEach(System.out::println);
        System.out.println();

    }
}
