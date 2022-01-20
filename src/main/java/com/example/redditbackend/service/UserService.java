package com.example.redditbackend.service;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import com.example.redditbackend.request.*;
import com.example.redditbackend.response.*;
import com.example.redditbackend.utility.SHA256;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Log4j2
public class UserService implements UserDetailsService {
    @Autowired
    protected UserTableRepository userRepo;

    @Autowired
    protected CommunityTableRepository communityRepo;

    @Autowired
    protected CoOwnerUserCommunityTableRepository coOwnerRepo;

    @Autowired
    protected ModUserCommunityTableRepository modRepo;

    @Autowired
    protected NormalUserCommunityTableRepository normalRepo;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) throws Exception{
        try{
            UserTable checkExistingUser = userRepo.findByEmail(registerRequest.getEmail());
            if(checkExistingUser != null)
                throw new Exception("User with email "+registerRequest.getEmail()+" already exists");
            checkExistingUser = userRepo.findByUsername(registerRequest.getUsername());
            if(checkExistingUser != null)
                throw new Exception("User with username "+registerRequest.getUsername()+" already exists");
            UserTable newUser = new UserTable();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setHashedPassword(SHA256.toHexString(SHA256.getSHA(registerRequest.getPassword())));
            newUser.setJoinDate(new Date());
            newUser.setLastLoggedIn(new Date());
            UserTable savedUser = userRepo.save(newUser);
            return new RegisterResponse("User registered successfully", savedUser.getUsername(), savedUser.getUserId());
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception(e.toString());
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) throws Exception{
        try{
            UserTable checkUser = userRepo.findByUsername(loginRequest.getUsername());
            if(checkUser == null)
                throw new Exception("Unable to find username");
            String hashedPassword = SHA256.toHexString(SHA256.getSHA(loginRequest.getPassword()));
            if(hashedPassword.equals(checkUser.getHashedPassword()))
                return new LoginResponse("User logged in", checkUser.getUserId(), checkUser.getUsername());
            throw new Exception("Password don't match");
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception(e.toString());
        }
    }

    @Transactional
    public CreateCommunityResponse createCommunity(CommunityRequest communityRequest) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(communityRequest.getCreatorId());
            if(!checkUser.isPresent())
                throw new Exception("Unable to find creator id");
            CommunityTable checkExistingCommunity = communityRepo.findByCommunityName(communityRequest.getCommunityName());
            if(checkExistingCommunity != null)
                throw new Exception("Community name already exists");
            CommunityTable communityTable = new CommunityTable();
            communityTable.setCommunityName(communityRequest.getCommunityName());
            communityTable.setCurrentOwner(checkUser.get());
            communityTable.setCreationDate(new Date());
            communityTable.setRules(communityRequest.getRules());
            communityTable.setCreatorId(checkUser.get());
            communityTable.setCommunityDescription(communityRequest.getCommunityDescription());
            log.error(checkUser.get().getUserId());
            CommunityTable savedCommunity=communityRepo.save(communityTable);

            NormalUserCommunityTable normalUserCommunityTable = new NormalUserCommunityTable();
            normalUserCommunityTable.setCommunityId(savedCommunity);
            normalUserCommunityTable.setUserId(checkUser.get());
            normalUserCommunityTable.setIsUserBanned(false);
            normalUserCommunityTable.setJoinDate(new Date());
            NormalUserCommunityTable savedUser = normalRepo.save(normalUserCommunityTable);

            CreateCommunityResponse response = new CreateCommunityResponse(savedCommunity.getCommunityId(), savedCommunity.getCommunityName(), savedCommunity.getCreationDate(),
                    savedCommunity.getCreatorId().getUserId(), savedCommunity.getCurrentOwner().getUserId(), savedUser.getUserId().getUserId(),
                    savedCommunity.getCommunityDescription(), savedCommunity.getRules());

            return response;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to create community due to: "+e.toString());
        }
    }

    @Transactional
    public List<CommunityResponse> getAllCommunities() throws Exception{
        try{
            List<CommunityTable> communityTableList =  communityRepo.findAll();
            List<CommunityResponse> result = new ArrayList<>();
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId(), c.getCommunityDescription());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    @Transactional
    public List<CommunityResponse> getAllCommunities(String username) throws Exception{
        try{
            UserTable userTable = userRepo.findByUsername(username);
            if(userTable == null)
                throw new Exception("Unable to find username");
            List<CommunityResponse> result = new ArrayList<>();
            List<CommunityTable> communityTableList = communityRepo.findByCurrentOwner(userTable);
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId(), c.getCommunityDescription());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    @Transactional
    public List<CommunityResponse> getAllCommunitiesByCreatorId(String username) throws Exception{
        try{
            UserTable userTable = userRepo.findByUsername(username);
            if(userTable == null)
                throw new Exception("Unable to find username");
            List<CommunityResponse> result = new ArrayList<>();
            List<CommunityTable> communityTableList = communityRepo.findByCreatorId(userTable);
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId(), c.getCommunityDescription());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    @Transactional
    public JoinCommunityResponse joinCommunity(Integer userId, Integer communityId) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not found");
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not found");
            NormalUserCommunityTable existingUser = normalRepo.findByUserIdAndCommunityId(checkUser.get(), checkCommunity.get());
            if(existingUser != null)
                throw new Exception("User already exists in the community");
            NormalUserCommunityTable normalUserCommunityTable = new NormalUserCommunityTable();
            normalUserCommunityTable.setUserId(checkUser.get());
            normalUserCommunityTable.setCommunityId(checkCommunity.get());
            normalUserCommunityTable.setIsUserBanned(false);
            normalUserCommunityTable.setJoinDate(new Date());
            NormalUserCommunityTable savedJoinUser = normalRepo.save(normalUserCommunityTable);

            JoinCommunityResponse response = new JoinCommunityResponse(savedJoinUser.getNormalUserCommunityId(), savedJoinUser.getUserId().getUserId(),
                    savedJoinUser.getCommunityId().getCommunityId(), savedJoinUser.getJoinDate(), savedJoinUser.getIsUserBanned());

            return response;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to join community due to: "+e.toString());
        }
    }

    @Transactional
    public PromoteToModResponse promoteToMod(Integer userId, Integer communityId, Integer promoterId) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not found");
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not found");
            Optional<UserTable> checkPromoter = userRepo.findById(promoterId);
            if(!checkPromoter.isPresent())
                throw new Exception("Promoter not present");
            CommunityTable checkOwner = communityRepo.findByCurrentOwnerAndCommunityId(checkPromoter.get(), checkCommunity.get().getCommunityId());
            CoOwnerUserCommunityTable checkCoOwner = coOwnerRepo.findByUserIdAndCommunityId(checkPromoter.get(), checkCommunity.get());
            if(checkOwner == null && checkCoOwner == null)
                throw new Exception("Unable to find userid either as a owner or a co-owner of the community provided");
            NormalUserCommunityTable checkUserInCommunity = normalRepo.findByUserIdAndCommunityId(checkUser.get(), checkCommunity.get());
            if(checkUserInCommunity==null)
                throw new Exception("Unable to find user in the community");
            ModUserCommunityTable checkExistingMod = modRepo.findByCommunityTableAndUserId(checkCommunity.get(), checkUser.get());
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
                savedMod = modRepo.save(modUserCommunityTable);
            }else if(!checkExistingMod.getCurrentlyActiveMod()){
                if(checkOwner == null)
                    checkExistingMod.setPromotedBy(checkCoOwner.getUserId());
                else
                    checkExistingMod.setPromotedBy(checkOwner.getCurrentOwner());
                checkExistingMod.setBecomeModDate(new Date());
                checkExistingMod.setStatusChange(new Date());
                checkExistingMod.setCurrentlyActiveMod(true);
                savedMod = modRepo.save(checkExistingMod);
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

    @Transactional
    public PromoteToModResponse demoteFromMod(Integer userId, Integer communityId, Integer demoterId) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not found");
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not found");
            Optional<UserTable> checkDemoter = userRepo.findById(demoterId);
            if(!checkDemoter.isPresent())
                throw new Exception("Demoter not present");
            CommunityTable checkOwner = communityRepo.findByCurrentOwnerAndCommunityId(checkDemoter.get(), checkCommunity.get().getCommunityId());
            CoOwnerUserCommunityTable checkCoOwner = coOwnerRepo.findByUserIdAndCommunityId(checkDemoter.get(), checkCommunity.get());
            if(checkOwner == null && checkCoOwner == null)
                throw new Exception("Unable to find userid either as a owner or a co-owner of the community provided");
            ModUserCommunityTable checkMod = modRepo.findByCommunityTableAndUserId(checkCommunity.get(), checkUser.get());
            if(checkMod == null)
                throw new Exception("Unable to find the mod in the community mentioned");
            if(!checkMod.getCurrentlyActiveMod())
                throw new Exception("Mod not available");
            checkMod.setCurrentlyActiveMod(false);
            checkMod.setStatusChange(new Date());
            checkMod.setPromotedBy(checkDemoter.get());
            ModUserCommunityTable savedMod = modRepo.save(checkMod);

            PromoteToModResponse demotedFromModResponse = new PromoteToModResponse(savedMod.getModUserId(), savedMod.getUserId().getUserId(), savedMod.getCommunityTable()
                    .getCommunityId(), savedMod.getBecomeModDate(), savedMod.getPromotedBy().getUserId(), savedMod.getCurrentlyActiveMod(), savedMod.getStatusChange());
            return demotedFromModResponse;
        }catch(Exception e){
            log.error(e);
            throw new Exception("Unable to demote from mod because: "+ e);
        }
    }

    @Transactional
    public PromoterToCoOwnerResponse promoteToCoOwner(Integer userId, Integer communityId, Integer promoterId) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not present");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not present");
            CommunityTable community = checkCommunity.get();
            Optional<UserTable> checkPromoter = userRepo.findById(promoterId);
            if(!checkPromoter.isPresent())
                throw new Exception("Promoter not present");
            UserTable promoter = checkPromoter.get();
            CommunityTable checkCommunityAndOwner = communityRepo.findByCurrentOwnerAndCommunityId(promoter, community.getCommunityId());
            if(checkCommunityAndOwner == null)
                throw new Exception("Unable to find owner as the promoter");
            NormalUserCommunityTable checkUserInCommunity = normalRepo.findByUserIdAndCommunityId(user, community);
            if(checkUserInCommunity==null)
                throw new Exception("Unable to find user in the community");
            CoOwnerUserCommunityTable checkExistingCoOwner = coOwnerRepo.findByUserIdAndCommunityId(user, community);
            CoOwnerUserCommunityTable savedCoOwner;
            if(checkExistingCoOwner==null){
                CoOwnerUserCommunityTable newCoOwner = new CoOwnerUserCommunityTable();
                newCoOwner.setBecomeCoOwnerDate(new Date());
                newCoOwner.setUserId(user);
                newCoOwner.setCommunityId(community);
                newCoOwner.setCurrentlyActive(true);
                savedCoOwner = coOwnerRepo.save(newCoOwner);
            }else if(!checkExistingCoOwner.getCurrentlyActive()){
                checkExistingCoOwner.setCurrentlyActive(true);
                checkExistingCoOwner.setBecomeCoOwnerDate(new Date());
                savedCoOwner = coOwnerRepo.save(checkExistingCoOwner);
            }else{
                throw new Exception("Co owner already exists in the database");
            }

            ModUserCommunityTable checkMod = modRepo.findByCommunityTableAndUserId(community, user);
            if(checkMod!=null){
                checkMod.setCurrentlyActiveMod(false);
                checkMod.setStatusChange(new Date());
                checkMod.setPromotedBy(promoter);
                modRepo.save(checkMod);
            }

            PromoterToCoOwnerResponse response = new PromoterToCoOwnerResponse(savedCoOwner.getCoOwnerUserCommunityId(), savedCoOwner.getUserId().getUserId(),
                    savedCoOwner.getCommunityId().getCommunityId(), savedCoOwner.getBecomeCoOwnerDate(), savedCoOwner.getCurrentlyActive(), savedCoOwner.getStatusChangeDate());

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to promote to co owner because "+e);
        }
    }

    @Transactional
    public DemoteFromCoOwnerResponse demoteFromCoOwner(Integer userId, Integer communityId, Integer demoterId, String toMod) throws Exception{
        try{
            if(!toMod.toLowerCase(Locale.ROOT).equals("y") && !toMod.toLowerCase(Locale.ROOT).equals("n"))
                throw new Exception("ToMod has an incorrect value");
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("User not present");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Community not present");
            CommunityTable community = checkCommunity.get();
            Optional<UserTable> checkDemoterId = userRepo.findById(demoterId);
            if(!checkDemoterId.isPresent())
                throw new Exception("Promoter not present");
            UserTable demoter = checkDemoterId.get();

            CommunityTable checkOwner = communityRepo.findByCurrentOwnerAndCommunityId(demoter, community.getCommunityId());
            if(checkOwner == null)
                throw new Exception("Demoter not the owner of the community hence cannot demote");
            CoOwnerUserCommunityTable checkUserId = coOwnerRepo.findByUserIdAndCommunityId(user, community);
            if(checkUserId == null)
                throw new Exception("User not found as the co owner of the community");

            checkUserId.setCurrentlyActive(false);
            checkUserId.setStatusChangeDate(new Date());
            CoOwnerUserCommunityTable savedUser = coOwnerRepo.save(checkUserId);

            DemoteFromCoOwnerResponse response = new DemoteFromCoOwnerResponse();
            response.setCoOwnerUserCommunityId(savedUser.getCoOwnerUserCommunityId());
            response.setUserId(savedUser.getUserId().getUserId());
            response.setCommunityId(savedUser.getCommunityId().getCommunityId());
            response.setBecomeCoOwnerDate(savedUser.getBecomeCoOwnerDate());
            response.setCurrentlyActive(savedUser.getCurrentlyActive());
            response.setStatusChangeDate(savedUser.getStatusChangeDate());

            if(toMod.toLowerCase(Locale.ROOT).equals("y")){
                ModUserCommunityTable checkMod = modRepo.findByCommunityTableAndUserId(community, user);
                if(checkMod == null){
                    ModUserCommunityTable modUserCommunityTable = new ModUserCommunityTable();
                    modUserCommunityTable.setCommunityTable(community);
                    modUserCommunityTable.setUserId(user);
                    modUserCommunityTable.setBecomeModDate(new Date());
                    modUserCommunityTable.setPromotedBy(demoter);
                    modUserCommunityTable.setStatusChange(new Date());
                    modUserCommunityTable.setCurrentlyActiveMod(true);
                    modRepo.save(modUserCommunityTable);
                    response.setIsCurrentlyMod(true);
                }else{
                    checkMod.setCurrentlyActiveMod(true);
                    checkMod.setBecomeModDate(new Date());
                    checkMod.setStatusChange(new Date());
                    checkMod.setPromotedBy(demoter);
                    modRepo.save(checkMod);
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

    @Transactional
    public BanPersonResponse banUser(BanRequest banRequest) throws Exception{
        try{
            if(banRequest.getUserToBeBanned().equals(banRequest.getPersonBanning()))
                throw new Exception("Cannot ban your self i.e. the owner of the community");
            Optional<UserTable> checkUserToBeBanned = userRepo.findById(banRequest.getUserToBeBanned());
            if(!checkUserToBeBanned.isPresent())
                throw new Exception("Unable to find user");
            UserTable userToBeBanned = checkUserToBeBanned.get();
            Optional<CommunityTable> checkCommunityId = communityRepo.findById(banRequest.getCommunityFromWhichToBeBanned());
            if(!checkCommunityId.isPresent())
                throw new Exception("Community not present");
            CommunityTable community = checkCommunityId.get();
            Optional<UserTable> checkPersonBanning = userRepo.findById(banRequest.getPersonBanning());
            if(!checkPersonBanning.isPresent())
                throw new Exception("Person banning not present");
            UserTable personBanning = checkPersonBanning.get();
            NormalUserCommunityTable checkNormalUser = normalRepo.findByUserIdAndCommunityId(userToBeBanned, community);
            if(checkNormalUser == null)
                throw new Exception("Unable to find the person in the community");

            ModUserCommunityTable checkMod = modRepo.findByCommunityTableAndUserId(community, personBanning);
            CoOwnerUserCommunityTable checkCoOwner = coOwnerRepo.findByUserIdAndCommunityId(personBanning, community);
            CommunityTable checkOwner = communityRepo.findByCurrentOwnerAndCommunityId(personBanning, community.getCommunityId());

            if(checkMod == null && checkCoOwner == null && checkOwner == null)
                throw new Exception("Not enough permissions to ban");

            CoOwnerUserCommunityTable banAsCoOwner = coOwnerRepo.findByUserIdAndCommunityId(userToBeBanned, community);
            if(banAsCoOwner != null){
                banAsCoOwner.setCurrentlyActive(false);
                banAsCoOwner.setStatusChangeDate(new Date());
                coOwnerRepo.save(banAsCoOwner);
            }
            ModUserCommunityTable banAsMod = modRepo.findByCommunityTableAndUserId(community, userToBeBanned);
            if(banAsMod != null){
                banAsMod.setCurrentlyActiveMod(false);
                banAsMod.setStatusChange(new Date());
                banAsMod.setPromotedBy(personBanning);
                modRepo.save(banAsMod);
            }

            checkNormalUser.setIsUserBanned(true);
            checkNormalUser.setBannedBy(personBanning);
            checkNormalUser.setBanReason(banRequest.getBanReason());
            checkNormalUser.setDateBanned(new Date());
            NormalUserCommunityTable savedBannedUser = normalRepo.save(checkNormalUser);

            BanPersonResponse response = new BanPersonResponse(savedBannedUser.getNormalUserCommunityId(), savedBannedUser.getUserId().getUserId()
                    , savedBannedUser.getCommunityId().getCommunityId(), savedBannedUser.getJoinDate(), savedBannedUser.getIsUserBanned(), savedBannedUser.getBanReason(),
                    savedBannedUser.getDateBanned());

            return response;

        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to ban person because: "+e);
        }
    }

    @Transactional
    public UnBanUserResponse unBanUser(Integer userId, Integer communityId, Integer unBanningId) throws Exception{
        try{
            Optional<UserTable> checkUserId = userRepo.findById(userId);
            if(!checkUserId.isPresent())
                throw new Exception("Unable to find userId");
            UserTable user = checkUserId.get();
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Unable to find community");
            CommunityTable community = checkCommunity.get();
            Optional<UserTable> checkUnBanner = userRepo.findById(unBanningId);
            if(!checkUnBanner.isPresent())
                throw new Exception("Unable to find un banning id");
            UserTable unBanner = checkUnBanner.get();
            ModUserCommunityTable checkMod = modRepo.findByCommunityTableAndUserId(community, unBanner);
            CoOwnerUserCommunityTable checkCoOwner = coOwnerRepo.findByUserIdAndCommunityId(unBanner, community);
            CommunityTable checkOwner = communityRepo.findByCurrentOwnerAndCommunityId(unBanner, community.getCommunityId());
            if(checkOwner == null && checkCoOwner == null && checkMod == null)
                throw new Exception("Not enough permissions to ban a user");
            NormalUserCommunityTable checkUser = normalRepo.findByUserIdAndCommunityId(user, community);
            if(checkUser == null)
                throw new Exception("Unable to find user in the community");
            if(!checkUser.getIsUserBanned())
                throw new Exception("No user to ban");
            checkUser.setIsUserBanned(false);
            checkUser.setDateBanned(new Date());
            checkUser.setBannedBy(unBanner);
            NormalUserCommunityTable savedUser = normalRepo.save(checkUser);

            UnBanUserResponse response = new UnBanUserResponse(savedUser.getNormalUserCommunityId(), savedUser.getUserId().getUserId(), savedUser.getCommunityId().getCommunityId(),
                    savedUser.getJoinDate(), savedUser.getIsUserBanned(), savedUser.getBanReason(), savedUser.getDateBanned(), savedUser.getBannedBy().getUserId());

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to unban user because: "+e);
        }
    }

    @Transactional
    public LeaveCommunityResponse leaveCommunity(Integer userId, Integer communityId) throws Exception{
        try{
            LeaveCommunityResponse response = new LeaveCommunityResponse();
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("Unable to find user");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Unable to find community");
            CommunityTable community = checkCommunity.get();
            NormalUserCommunityTable checkNormalUser = normalRepo.findByUserIdAndCommunityId(user, community);
            if(checkNormalUser== null)
                throw new Exception("Cannot leave the community as user is not a part of it");
            if(checkNormalUser.getIsUserBanned())
                throw new Exception("User is banned");
            CoOwnerUserCommunityTable coOwner = coOwnerRepo.findByUserIdAndCommunityId(user, community);
            if(coOwner!=null){
                response.setCoOwnerIdDeleted(coOwner.getCoOwnerUserCommunityId());
                coOwnerRepo.delete(coOwner);
            }
            ModUserCommunityTable mod = modRepo.findByCommunityTableAndUserId(community, user);
            if(mod != null){
                response.setModUserIdDeleted(mod.getModUserId());
                modRepo.delete(mod);
            }
            response.setCommunityId(checkNormalUser.getCommunityId().getCommunityId());
            response.setNormalUserIdDeleted(checkNormalUser.getNormalUserCommunityId());
            response.setUserId(checkNormalUser.getUserId().getUserId());
            normalRepo.delete(checkNormalUser);
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to leave community because: "+e);
        }
    }

    @Transactional
    public GetCommunityDetailsResponse getCommunityDetails(Integer userId, Integer communityId) throws Exception{
        try{
            GetCommunityDetailsResponse response = new GetCommunityDetailsResponse();
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("Unable to find the user");
            UserTable user = checkUser.get();
            Optional<CommunityTable> checkCommunity = communityRepo.findById(communityId);
            if(!checkCommunity.isPresent())
                throw new Exception("Unable to find community");
            CommunityTable community = checkCommunity.get();

            CommunityTable checkOwner = communityRepo.findByCurrentOwnerAndCommunityId(user, community.getCommunityId());
            if(checkOwner == null)
                throw new Exception("User doesn't have enough permission for this action");

            response.setCommunityId(checkOwner.getCommunityId());
            response.setCommunityName(checkOwner.getCommunityName());
            response.setCommunityDescription(checkOwner.getCommunityDescription());
            response.setCreationDate(checkOwner.getCreationDate());
            response.setRules(checkOwner.getRules());
            response.setCreatorId(checkOwner.getCreatorId().getUserId());
            response.setCreatorName(checkOwner.getCreatorId().getUsername());
            response.setCurrentOwnerId(checkOwner.getCurrentOwner().getUserId());
            response.setCurrentOwnerName(checkOwner.getCurrentOwner().getUsername());

            List<NormalUserDescriptionForDetails> normalUsersDetails = new ArrayList<>();
            List<NormalUserCommunityTable> listOfNormalUsers = normalRepo.findAll();
            for(NormalUserCommunityTable n : listOfNormalUsers){
                NormalUserDescriptionForDetails temp = new NormalUserDescriptionForDetails();
                temp.setNormalUserId(n.getUserId().getUserId());
                temp.setNormalUsername(n.getUserId().getUsername());
                temp.setIsUserBanned(n.getIsUserBanned());
                temp.setBanReason(n.getBanReason());
                normalUsersDetails.add(temp);
            }
            response.setListOfUsers(normalUsersDetails);

            List<ModUserDescriptionForDetails> modUsersDetails = new ArrayList<>();
            List<ModUserCommunityTable> listOfModUsers = modRepo.findAll();
            for(ModUserCommunityTable n : listOfModUsers){
                ModUserDescriptionForDetails temp = new ModUserDescriptionForDetails();
                temp.setModId(n.getUserId().getUserId());
                temp.setModUsername(n.getUserId().getUsername());
                temp.setIsCurrentlyActive(n.getCurrentlyActiveMod());
                modUsersDetails.add(temp);
            }
            response.setListOfMods(modUsersDetails);

            List<CoOwnerDescriptionForDetails> coOwnerDetails = new ArrayList<>();
            List<CoOwnerUserCommunityTable> listOfCoOwnerUsers = coOwnerRepo.findAll();
            for(CoOwnerUserCommunityTable n : listOfCoOwnerUsers){
                CoOwnerDescriptionForDetails temp = new CoOwnerDescriptionForDetails();
                temp.setCoOwnerId(n.getUserId().getUserId());
                temp.setIsCurrentlyActive(n.getCurrentlyActive());
                temp.setCoOwnerUsername(n.getUserId().getUsername());
                coOwnerDetails.add(temp);
            }
            response.setListOfCoOwners(coOwnerDetails);

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to fetch community details because: "+e);
        }
    }

    @Transactional
    public ModifyCommunityResponse modifyCommunityRequest(ModifyCommunityRequest modifyCommunityRequest) throws Exception {
        try{
            Optional<CommunityTable> checkCommunity = communityRepo.findById(modifyCommunityRequest.getCommunityId());
            if(!checkCommunity.isPresent())
                throw new Exception("Unable to fetch community");
            CommunityTable community = checkCommunity.get();

            community.setCommunityName(modifyCommunityRequest.getName());
            community.setRules(modifyCommunityRequest.getRules());
            community.setCommunityDescription(modifyCommunityRequest.getCommunityDescription());

            CommunityTable savedCommunity = communityRepo.save(community);

            ModifyCommunityResponse response = new ModifyCommunityResponse(savedCommunity.getCommunityId(), savedCommunity.getCommunityName(), savedCommunity.getCommunityDescription()
            , savedCommunity.getCreationDate(), savedCommunity.getRules(), savedCommunity.getCreatorId(), savedCommunity.getCurrentOwner());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to modify community because: "+e);
        }
    }

    @Transactional
    public List<FetchCommunitiesResponse> fetchCommunities(Integer userId) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("Unable to find user");
            UserTable user = checkUser.get();
            List<Object> communitiesFromDB = communityRepo.findCommunitiesNotJoined(user.getUserId());
            if(communitiesFromDB.size()==0)
                return new ArrayList<>();
            List<FetchCommunitiesResponse> response = new ArrayList<>();
            for(int i=0; i<communitiesFromDB.size();i++){
                FetchCommunitiesResponse temp = new FetchCommunitiesResponse();
                temp.setCommunityId((Integer) ((Object[])communitiesFromDB.get(i))[0]);
                temp.setCommunityName(((Object[])communitiesFromDB.get(i))[1].toString());
                temp.setCommunityDescription(((Object[])communitiesFromDB.get(i))[2].toString());
                List<Object> getRulesFromDB = communityRepo.fetchRules(temp.getCommunityId());
                if(getRulesFromDB.size()!=0){
                    log.error(getRulesFromDB.size());
                    List<String> rules = new ArrayList<>();
                    for(int j=0;j<getRulesFromDB.size();j++){
                        String s = getRulesFromDB.get(j).toString();
                        rules.add(s);
                    }
                    temp.setRules(rules);
                }
                response.add(temp);
            }
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to fetch communities because: "+e);
        }
    }

    @Transactional
    public List<FetchCommunitiesResponse> fetchCommunitiesOfUser(Integer userId) throws Exception{
        try{
            Optional<UserTable> checkUser = userRepo.findById(userId);
            if(!checkUser.isPresent())
                throw new Exception("Unable to find user");
            UserTable user = checkUser.get();
            List<Object> communitiesFromDB = communityRepo.findCommunitiesJoined(user.getUserId());
            if(communitiesFromDB.size()==0)
                return new ArrayList<>();
            List<FetchCommunitiesResponse> response = new ArrayList<>();
            for(int i=0; i<communitiesFromDB.size();i++){
                FetchCommunitiesResponse temp = new FetchCommunitiesResponse();
                temp.setCommunityId((Integer) ((Object[])communitiesFromDB.get(i))[0]);
                temp.setCommunityName(((Object[])communitiesFromDB.get(i))[1].toString());
                temp.setCommunityDescription(((Object[])communitiesFromDB.get(i))[2].toString());
                List<Object> getRulesFromDB = communityRepo.fetchRules(temp.getCommunityId());
                if(getRulesFromDB.size()!=0){
                    log.error(getRulesFromDB.size());
                    List<String> rules = new ArrayList<>();
                    for(int j=0;j<getRulesFromDB.size();j++){
                        String s = getRulesFromDB.get(j).toString();
                        rules.add(s);
                    }
                    temp.setRules(rules);
                }
                response.add(temp);
            }
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to fetch communities because: "+e);
        }
    }

    @Transactional
    public TransferOwnershipResponse transferOwnership(Integer newOwner, Integer communityId, Integer oldOwner) throws Exception {
        try{
            if(newOwner.equals(oldOwner))
                throw new Exception("Old and new owner cannot be same");
            Optional<UserTable> newOwnerId = userRepo.findById(newOwner);
            if(!newOwnerId.isPresent())
                throw new Exception("new owner not present");
            UserTable newOwnerUser = newOwnerId.get();
            Optional<UserTable> oldOwnerId = userRepo.findById(oldOwner);
            if(!oldOwnerId.isPresent())
                throw new Exception("Unable to find old owner");
            UserTable oldOwnerUser = oldOwnerId.get();
            Optional<CommunityTable> communityTable = communityRepo.findById(communityId);
            if(!communityTable.isPresent())
                throw new Exception("Unable to find community");
            CommunityTable community = communityTable.get();
            CommunityTable currentOwner = communityRepo.findByCurrentOwnerAndCommunityId(oldOwnerUser, community.getCommunityId());
            if(currentOwner==null)
                throw new Exception("Current owner not found for the community mentioned");
            NormalUserCommunityTable userInCommunity = normalRepo.findByUserIdAndCommunityId(newOwnerUser, community);
            if(userInCommunity==null)
                throw new Exception("Unable to find the user in community");
            ModUserCommunityTable checkMod = modRepo.findByCommunityTableAndUserId(community, newOwnerUser);
            CoOwnerUserCommunityTable checkCo = coOwnerRepo.findByUserIdAndCommunityId(newOwnerUser, community);
            if(checkMod != null)
                modRepo.delete(checkMod);
            if(checkCo != null)
                coOwnerRepo.delete(checkCo);
            community.setCurrentOwner(newOwnerUser);
            CommunityTable savedCommunity = communityRepo.save(community);
            CoOwnerUserCommunityTable newCoOwner = new CoOwnerUserCommunityTable();
            newCoOwner.setUserId(oldOwnerUser);
            newCoOwner.setCommunityId(community);
            newCoOwner.setBecomeCoOwnerDate(new Date());
            newCoOwner.setCurrentlyActive(true);
            newCoOwner.setStatusChangeDate(new Date());
            CoOwnerUserCommunityTable savedNewCoOwner = coOwnerRepo.save(newCoOwner);

            TransferOwnershipResponse response = new TransferOwnershipResponse(savedCommunity.getCurrentOwner().getUserId(), oldOwnerUser.getUserId(), savedCommunity.getCommunityId(),
                    community.getCommunityName(), savedNewCoOwner.getStatusChangeDate());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to transfer ownership because: "+e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserTable user = userRepo.findByUsername(username);
        return new User(user.getUsername(), user.getHashedPassword(), new ArrayList<>());
    }
}
