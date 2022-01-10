package com.example.redditbackend.service;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import com.example.redditbackend.request.BanRequest;
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
import java.util.*;

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

            CreateCommunityResponse response = new CreateCommunityResponse(savedCommunity.getCommunityId(), savedCommunity.getCommunityName(), savedCommunity.getCreationDate(),
                    savedCommunity.getCreatorId().getUserId(), savedCommunity.getCurrentOwner().getUserId(), savedUser.getUserId().getUserId(), savedCommunity.getRules());

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

            ModUserCommunityTable checkMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(community, user);
            if(checkMod!=null){
                checkMod.setCurrentlyActiveMod(false);
                checkMod.setStatusChange(new Date());
                checkMod.setPromotedBy(promoter);
                modUserCommunityTableRepository.save(checkMod);
            }

            PromoterToCoOwnerResponse response = new PromoterToCoOwnerResponse(savedCoOwner.getCoOwnerUserCommunityId(), savedCoOwner.getUserId().getUserId(),
                    savedCoOwner.getCommunityId().getCommunityId(), savedCoOwner.getBecomeCoOwnerDate(), savedCoOwner.getCurrentlyActive(), savedCoOwner.getStatusChangeDate());

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to promote to co owner because "+e);
        }
    }

    public DemoteFromCoOwnerResponse demoteFromCoOwner(Integer userId, Integer communityId, Integer demoterId, String toMod) throws Exception{
        try{
            if(!toMod.toLowerCase(Locale.ROOT).equals("y") && !toMod.toLowerCase(Locale.ROOT).equals("n"))
                throw new Exception("ToMod has an incorrect value");
            Optional<UserTable> checkUser = userTableRepository.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not present");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not present");
            CommunityTable community = checkCommunity.get();
            Optional<UserTable> checkDemoterId = userTableRepository.findById(demoterId);
            if(!checkDemoterId.isPresent())
                throw new Exception("Promoter not present");
            UserTable demoter = checkDemoterId.get();

            CommunityTable checkOwner = communityTableRepository.findByCurrentOwnerAndCommunityId(demoter, community.getCommunityId());
            if(checkOwner == null)
                throw new Exception("Demoter not the owner of the community hence cannot demote");
            CoOwnerUserCommunityTable checkUserId = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(user, community);
            if(checkUserId == null)
                throw new Exception("User not found as the co owner of the community");

            checkUserId.setCurrentlyActive(false);
            checkUserId.setStatusChangeDate(new Date());
            CoOwnerUserCommunityTable savedUser = coOwnerUserCommunityTableRepository.save(checkUserId);

            DemoteFromCoOwnerResponse response = new DemoteFromCoOwnerResponse();
            response.setCoOwnerUserCommunityId(savedUser.getCoOwnerUserCommunityId());
            response.setUserId(savedUser.getUserId().getUserId());
            response.setCommunityId(savedUser.getCommunityId().getCommunityId());
            response.setBecomeCoOwnerDate(savedUser.getBecomeCoOwnerDate());
            response.setCurrentlyActive(savedUser.getCurrentlyActive());
            response.setStatusChangeDate(savedUser.getStatusChangeDate());

            if(toMod.toLowerCase(Locale.ROOT).equals("y")){
                ModUserCommunityTable checkMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(community, user);
                if(checkMod == null){
                    ModUserCommunityTable modUserCommunityTable = new ModUserCommunityTable();
                    modUserCommunityTable.setCommunityTable(community);
                    modUserCommunityTable.setUserId(user);
                    modUserCommunityTable.setBecomeModDate(new Date());
                    modUserCommunityTable.setPromotedBy(demoter);
                    modUserCommunityTable.setStatusChange(new Date());
                    modUserCommunityTable.setCurrentlyActiveMod(true);
                    modUserCommunityTableRepository.save(modUserCommunityTable);
                    response.setIsCurrentlyMod(true);
                }else{
                    checkMod.setCurrentlyActiveMod(true);
                    checkMod.setBecomeModDate(new Date());
                    checkMod.setStatusChange(new Date());
                    checkMod.setPromotedBy(demoter);
                    modUserCommunityTableRepository.save(checkMod);
                    response.setIsCurrentlyMod(true);
                }
            }else{
                response.setIsCurrentlyMod(false);
            }

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to demote from co owner because "+e);
        }
    }

    public BanPersonResponse banUser(BanRequest banRequest) throws Exception{
        try{
            if(banRequest.getUserToBeBanned().equals(banRequest.getPersonBanning()))
                throw new Exception("Cannot ban your self i.e. the owner of the community");
            Optional<UserTable> checkUserToBeBanned = userTableRepository.findById(banRequest.getUserToBeBanned());
            if(!checkUserToBeBanned.isPresent())
                throw new Exception("Unable to find user");
            UserTable userToBeBanned = checkUserToBeBanned.get();
            Optional<CommunityTable> checkCommunityId = communityTableRepository.findById(banRequest.getCommunityFromWhichToBeBanned());
            if(!checkCommunityId.isPresent())
                throw new Exception("Community not present");
            CommunityTable community = checkCommunityId.get();
            Optional<UserTable> checkPersonBanning = userTableRepository.findById(banRequest.getPersonBanning());
            if(!checkPersonBanning.isPresent())
                throw new Exception("Person banning not present");
            UserTable personBanning = checkPersonBanning.get();
            NormalUserCommunityTable checkNormalUser = normalUserCommunityTableRepository.findByUserIdAndCommunityId(userToBeBanned, community);
            if(checkNormalUser == null)
                throw new Exception("Unable to find the person in the community");

            ModUserCommunityTable checkMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(community, personBanning);
            CoOwnerUserCommunityTable checkCoOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(personBanning, community);
            CommunityTable checkOwner = communityTableRepository.findByCurrentOwnerAndCommunityId(personBanning, community.getCommunityId());

            if(checkMod == null && checkCoOwner == null && checkOwner == null)
                throw new Exception("Not enough permissions to ban");

            CoOwnerUserCommunityTable banAsCoOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(userToBeBanned, community);
            if(banAsCoOwner != null){
                banAsCoOwner.setCurrentlyActive(false);
                banAsCoOwner.setStatusChangeDate(new Date());
                coOwnerUserCommunityTableRepository.save(banAsCoOwner);
            }
            ModUserCommunityTable banAsMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(community, userToBeBanned);
            if(banAsMod != null){
                banAsMod.setCurrentlyActiveMod(false);
                banAsMod.setStatusChange(new Date());
                banAsMod.setPromotedBy(personBanning);
                modUserCommunityTableRepository.save(banAsMod);
            }

            checkNormalUser.setIsUserBanned(true);
            checkNormalUser.setBannedBy(personBanning);
            checkNormalUser.setBanReason(banRequest.getBanReason());
            checkNormalUser.setDateBanned(new Date());
            NormalUserCommunityTable savedBannedUser = normalUserCommunityTableRepository.save(checkNormalUser);

            BanPersonResponse response = new BanPersonResponse(savedBannedUser.getNormalUserCommunityId(), savedBannedUser.getUserId().getUserId()
                    , savedBannedUser.getCommunityId().getCommunityId(), savedBannedUser.getJoinDate(), savedBannedUser.getIsUserBanned(), savedBannedUser.getBanReason(),
                    savedBannedUser.getDateBanned());

            return response;

        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to ban person because: "+e);
        }
    }

    public UnBanUserResponse unBanUser(Integer userId, Integer communityId, Integer unBanningId) throws Exception{
        try{
            Optional<UserTable> checkUserId = userTableRepository.findById(userId);
            if(!checkUserId.isPresent())
                throw new Exception("Unable to find userId");
            UserTable user = checkUserId.get();
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Unable to find community");
            CommunityTable community = checkCommunity.get();
            Optional<UserTable> checkUnBanner = userTableRepository.findById(unBanningId);
            if(!checkUnBanner.isPresent())
                throw new Exception("Unable to find un banning id");
            UserTable unBanner = checkUnBanner.get();
            ModUserCommunityTable checkMod = modUserCommunityTableRepository.findByCommunityTableAndUserId(community, unBanner);
            CoOwnerUserCommunityTable checkCoOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(unBanner, community);
            CommunityTable checkOwner = communityTableRepository.findByCurrentOwnerAndCommunityId(unBanner, community.getCommunityId());
            if(checkOwner == null && checkCoOwner == null && checkMod == null)
                throw new Exception("Not enough permissions to ban a user");
            NormalUserCommunityTable checkUser = normalUserCommunityTableRepository.findByUserIdAndCommunityId(user, community);
            if(checkUser == null)
                throw new Exception("Unable to find user in the community");
            if(!checkUser.getIsUserBanned())
                throw new Exception("No user to ban");
            checkUser.setIsUserBanned(false);
            checkUser.setDateBanned(new Date());
            checkUser.setBannedBy(unBanner);
            NormalUserCommunityTable savedUser = normalUserCommunityTableRepository.save(checkUser);

            UnBanUserResponse response = new UnBanUserResponse(savedUser.getNormalUserCommunityId(), savedUser.getUserId().getUserId(), savedUser.getCommunityId().getCommunityId(),
                    savedUser.getJoinDate(), savedUser.getIsUserBanned(), savedUser.getBanReason(), savedUser.getDateBanned(), savedUser.getBannedBy().getUserId());

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to unban user because: "+e);
        }
    }

    public LeaveCommunityResponse leaveCommunity(Integer userId, Integer communityId) throws Exception{
        try{
            LeaveCommunityResponse response = new LeaveCommunityResponse();
            Optional<UserTable> checkUser = userTableRepository.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("Unable to find user");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityTableRepository.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Unable to find community");
            CommunityTable community = checkCommunity.get();
            NormalUserCommunityTable checkNormalUser = normalUserCommunityTableRepository.findByUserIdAndCommunityId(user, community);
            if(checkNormalUser== null)
                throw new Exception("Cannot leave the community as user is not a part of it");
            if(checkNormalUser.getIsUserBanned())
                throw new Exception("User is banned");
            CoOwnerUserCommunityTable coOwner = coOwnerUserCommunityTableRepository.findByUserIdAndCommunityId(user, community);
            if(coOwner!=null){
                response.setCoOwnerIdDeleted(coOwner.getCoOwnerUserCommunityId());
                coOwnerUserCommunityTableRepository.delete(coOwner);
            }
            ModUserCommunityTable mod = modUserCommunityTableRepository.findByCommunityTableAndUserId(community, user);
            if(mod != null){
                response.setModUserIdDeleted(mod.getModUserId());
                modUserCommunityTableRepository.delete(mod);
            }
            response.setCommunityId(checkNormalUser.getCommunityId().getCommunityId());
            response.setNormalUserIdDeleted(checkNormalUser.getNormalUserCommunityId());
            response.setUserId(checkNormalUser.getUserId().getUserId());
            normalUserCommunityTableRepository.delete(checkNormalUser);
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to leave community because: "+e);
        }
    }
}
