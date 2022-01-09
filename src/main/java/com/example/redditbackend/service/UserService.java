package com.example.redditbackend.service;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import com.example.redditbackend.request.CommunityRequest;
import com.example.redditbackend.request.LoginRequest;
import com.example.redditbackend.request.RegisterRequest;
import com.example.redditbackend.response.*;
import com.example.redditbackend.utility.SHA256;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.annotation.ExceptionProxy;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserService {
    @Autowired
    private UserTableRepository userTableRepository;

    @Autowired
    private CommunityTableRepository communityTableRepository;

    @Autowired
    private CoOwnerUserCommunityTableRepository coOwnerUserCommunityTableRepository;

    @Autowired
    private ModUserCommunityTableRepository modUserCommunityTableRepository;

    @Autowired
    private NormalUserCommunityTableRepository normalUserCommunityTableRepository;

    public RegisterResponse register(RegisterRequest registerRequest) throws Exception{
        try{
            UserTable checkExistingUser = userTableRepository.findByEmail(registerRequest.getEmail());
            if(checkExistingUser != null)
                throw new Exception("User with email "+registerRequest.getEmail()+" already exists");
            checkExistingUser = userTableRepository.findByUsername(registerRequest.getUsername());
            if(checkExistingUser != null)
                throw new Exception("User with username "+registerRequest.getUsername()+" already exists");
            UserTable newUser = new UserTable();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setHashedPassword(SHA256.toHexString(SHA256.getSHA(registerRequest.getPassword())));
            newUser.setJoinDate(new Date());
            newUser.setLastLoggedIn(new Date());
            userTableRepository.save(newUser);
            return new RegisterResponse("User registered successfully");
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception(e.toString());
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws Exception{
        try{
            UserTable checkUser = userTableRepository.findByUsername(loginRequest.getUsername());
            if(checkUser == null)
                throw new Exception("Unable to find username");
            String hashedPassword = SHA256.toHexString(SHA256.getSHA(loginRequest.getPassword()));
            if(hashedPassword.equals(checkUser.getHashedPassword()))
                return new LoginResponse("User logged in");
            throw new Exception("Password don't match");
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception(e.toString());
        }
    }

    public CreateCommunityResponse createCommunity(CommunityRequest communityRequest) throws Exception{
        try{
            Optional<UserTable> checkUser = userTableRepository.findById(communityRequest.getCreatorId());
            if(!checkUser.isPresent())
                throw new Exception("Unable to find creator id");
            CommunityTable checkExistingCommunity = communityTableRepository.findByCommunityName(communityRequest.getCommunityName());
            if(checkExistingCommunity != null)
                throw new Exception("Community name already exists");
            CommunityTable communityTable = new CommunityTable();
            communityTable.setCommunityName(communityRequest.getCommunityName());
            communityTable.setCurrentOwner(checkUser.get());
            communityTable.setCreationDate(new Date());
            communityTable.setRules(communityRequest.getRules());
            communityTable.setCreatorId(checkUser.get());
            log.error(checkUser.get().getUserId());
            CommunityTable savedCommunity=communityTableRepository.save(communityTable);

            NormalUserCommunityTable normalUserCommunityTable = new NormalUserCommunityTable();
            normalUserCommunityTable.setCommunityId(savedCommunity);
            normalUserCommunityTable.setUserId(checkUser.get());
            normalUserCommunityTable.setIsUserBanned(false);
            normalUserCommunityTable.setJoinDate(new Date());
            NormalUserCommunityTable savedUser = normalUserCommunityTableRepository.save(normalUserCommunityTable);

            CreateCommunityResponse response = new CreateCommunityResponse(savedCommunity.getCommunityId(), savedCommunity.getCommunityName(),
                    savedCommunity.getCreationDate(), savedCommunity.getCreatorId().getUserId(), savedCommunity.getCurrentOwner().getUserId(), savedUser.getUserId().getUserId());

            return response;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to create community due to: "+e.toString());
        }
    }

    public List<CommunityResponse> getAllCommunities() throws Exception{
        try{
            List<CommunityTable> communityTableList =  communityTableRepository.findAll();
            List<CommunityResponse> result = new ArrayList<>();
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    public List<CommunityResponse> getAllCommunities(String username) throws Exception{
        try{
            UserTable userTable = userTableRepository.findByUsername(username);
            if(userTable == null)
                throw new Exception("Unable to find username");
            List<CommunityResponse> result = new ArrayList<>();
            List<CommunityTable> communityTableList = communityTableRepository.findByCurrentOwner(userTable);
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    public List<CommunityResponse> getAllCommunitiesByCreatorId(String username) throws Exception{
        try{
            UserTable userTable = userTableRepository.findByUsername(username);
            if(userTable == null)
                throw new Exception("Unable to find username");
            List<CommunityResponse> result = new ArrayList<>();
            List<CommunityTable> communityTableList = communityTableRepository.findByCreatorId(userTable);
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    public JoinCommunityResponse joinCommunity(Integer userId, Integer communityId) throws Exception{
        try{
            Optional<UserTable> checkUser = userTableRepository.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not found");
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not found");
            NormalUserCommunityTable existingUser = normalUserCommunityTableRepository.findByUserIdAndCommunityId(checkUser.get(), checkCommunity.get());
            if(existingUser != null)
                throw new Exception("User already exists in the community");
            NormalUserCommunityTable normalUserCommunityTable = new NormalUserCommunityTable();
            normalUserCommunityTable.setUserId(checkUser.get());
            normalUserCommunityTable.setCommunityId(checkCommunity.get());
            normalUserCommunityTable.setIsUserBanned(false);
            normalUserCommunityTable.setJoinDate(new Date());
            NormalUserCommunityTable savedJoinUser = normalUserCommunityTableRepository.save(normalUserCommunityTable);

            JoinCommunityResponse response = new JoinCommunityResponse(savedJoinUser.getNormalUserCommunityId(), savedJoinUser.getUserId().getUserId(),
                    savedJoinUser.getCommunityId().getCommunityId(), savedJoinUser.getJoinDate(), savedJoinUser.getIsUserBanned());

            return response;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to join community due to: "+e.toString());
        }
    }

    public PromoteToModResponse promoteToMod(Integer userId, Integer communityId, Integer promoterId) throws Exception{
        try{
            Optional<UserTable> checkUser = userTableRepository.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not found");
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not found");
            Optional<UserTable> checkPromoter = userTableRepository.findById(promoterId);
            if(!checkPromoter.isPresent())
                throw new Exception("Promoter not present");
            CommunityTable checkOwner = communityTableRepository.findByCurrentOwnerAndCommunityId(checkPromoter.get(), checkCommunity.get().getCommunityId());
            CoOwnerUserCommunityTable checkCoOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(checkPromoter.get(), checkCommunity.get());
            if(checkOwner == null && checkCoOwner == null)
                throw new Exception("Unable to find userid either as a owner or a co-owner of the community provided");
            NormalUserCommunityTable checkUserInCommunity = normalUserCommunityTableRepository.findByUserIdAndCommunityId(checkUser.get(), checkCommunity.get());
            if(checkUserInCommunity==null)
                throw new Exception("Unable to find user in the community");
            ModUserCommunityTable checkExistingMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(checkCommunity.get(), checkUser.get());
            if(checkExistingMod != null && checkExistingMod.getCurrentlyActiveMod())
                throw new Exception("Mod already exists");
            ModUserCommunityTable savedMod;
            if(checkExistingMod == null) {
                ModUserCommunityTable modUserCommunityTable = new ModUserCommunityTable();
                modUserCommunityTable.setCommunityTable(checkCommunity.get());
                modUserCommunityTable.setUserId(checkUser.get());
                modUserCommunityTable.setBecomeModDate(new Date());
                modUserCommunityTable.setCurrentlyActiveMod(true);
                modUserCommunityTable.setPromotedBy(checkPromoter.get());
                savedMod = modUserCommunityTableRepository.save(modUserCommunityTable);
            }else if(!checkExistingMod.getCurrentlyActiveMod()){
                if(checkOwner == null)
                    checkExistingMod.setPromotedBy(checkCoOwner.getUserId());
                else
                    checkExistingMod.setPromotedBy(checkOwner.getCurrentOwner());
                checkExistingMod.setBecomeModDate(new Date());
                checkExistingMod.setStatusChange(new Date());
                checkExistingMod.setCurrentlyActiveMod(true);
                savedMod = modUserCommunityTableRepository.save(checkExistingMod);
            }else{
                throw new Exception("Mod already exists");
            }

            PromoteToModResponse promoteToModResponse = new PromoteToModResponse(savedMod.getModUserId(), savedMod.getUserId().getUserId(), savedMod.getCommunityTable()
                    .getCommunityId(), savedMod.getBecomeModDate(), savedMod.getPromotedBy().getUserId(), savedMod.getCurrentlyActiveMod(), savedMod.getStatusChange());

            return promoteToModResponse;
        }catch(Exception e){
            log.error(e);
            throw new Exception("Unable to promote to mod because: "+e.toString());
        }
    }

    public PromoteToModResponse demoteFromMod(Integer userId, Integer communityId, Integer demoterId) throws Exception{
        try{
            Optional<UserTable> checkUser = userTableRepository.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not found");
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not found");
            Optional<UserTable> checkDemoter = userTableRepository.findById(demoterId);
            if(!checkDemoter.isPresent())
                throw new Exception("Demoter not present");
            CommunityTable checkOwner = communityTableRepository.findByCurrentOwnerAndCommunityId(checkDemoter.get(), checkCommunity.get().getCommunityId());
            CoOwnerUserCommunityTable checkCoOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(checkDemoter.get(), checkCommunity.get());
            if(checkOwner == null && checkCoOwner == null)
                throw new Exception("Unable to find userid either as a owner or a co-owner of the community provided");
            ModUserCommunityTable checkMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(checkCommunity.get(), checkUser.get());
            if(checkMod == null)
                throw new Exception("Unable to find the mod in the community mentioned");
            if(!checkMod.getCurrentlyActiveMod())
                throw new Exception("Mod not available");
            checkMod.setCurrentlyActiveMod(false);
            checkMod.setStatusChange(new Date());
            checkMod.setPromotedBy(checkDemoter.get());
            ModUserCommunityTable savedMod = modUserCommunityTableRepository.save(checkMod);

            PromoteToModResponse demotedFromModResponse = new PromoteToModResponse(savedMod.getModUserId(), savedMod.getUserId().getUserId(), savedMod.getCommunityTable()
                    .getCommunityId(), savedMod.getBecomeModDate(), savedMod.getPromotedBy().getUserId(), savedMod.getCurrentlyActiveMod(), savedMod.getStatusChange());
            return demotedFromModResponse;
        }catch(Exception e){
            log.error(e);
            throw new Exception("Unable to demote from mod because: "+ e);
        }
    }

    public PromoterToCoOwnerResponse promoteToCoOwner(Integer userId, Integer communityId, Integer promoterId) throws Exception{
        try{
            Optional<UserTable> checkUser = userTableRepository.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not present");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not present");
            CommunityTable community = checkCommunity.get();
            Optional<UserTable> checkPromoter = userTableRepository.findById(promoterId);
            if(!checkPromoter.isPresent())
                throw new Exception("Promoter not present");
            UserTable promoter = checkPromoter.get();
            CommunityTable checkCommunityAndOwner = communityTableRepository.findByCurrentOwnerAndCommunityId(promoter, community.getCommunityId());
            if(checkCommunityAndOwner == null)
                throw new Exception("Unable to find owner as the promoter");
            NormalUserCommunityTable checkUserInCommunity = normalUserCommunityTableRepository.findByUserIdAndCommunityId(user, community);
            if(checkUserInCommunity==null)
                throw new Exception("Unable to find user in the community");
            CoOwnerUserCommunityTable checkExistingCoOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(user, community);
            CoOwnerUserCommunityTable savedCoOwner;
            if(checkExistingCoOwner==null){
                CoOwnerUserCommunityTable newCoOwner = new CoOwnerUserCommunityTable();
                newCoOwner.setBecomeCoOwnerDate(new Date());
                newCoOwner.setUserId(user);
                newCoOwner.setCommunityId(community);
                newCoOwner.setCurrentlyActive(true);
                savedCoOwner = coOwnerUserCommunityTableRepository.save(newCoOwner);
            }else if(!checkExistingCoOwner.getCurrentlyActive()){
                checkExistingCoOwner.setCurrentlyActive(true);
                checkExistingCoOwner.setBecomeCoOwnerDate(new Date());
                savedCoOwner = coOwnerUserCommunityTableRepository.save(checkExistingCoOwner);
            }else{
                throw new Exception("Co owner already exists in the database");
            }
            PromoterToCoOwnerResponse response = new PromoterToCoOwnerResponse(savedCoOwner.getCoOwnerUserCommunityId(), savedCoOwner.getUserId().getUserId(),
                    savedCoOwner.getCommunityId().getCommunityId(), savedCoOwner.getBecomeCoOwnerDate(), savedCoOwner.getCurrentlyActive(), savedCoOwner.getStatusChangeDate());

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to promote to co owner because "+e);
        }
    }
}
