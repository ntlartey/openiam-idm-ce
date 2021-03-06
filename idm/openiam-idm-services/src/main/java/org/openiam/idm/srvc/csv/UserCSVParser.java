package org.openiam.idm.srvc.csv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.csv.constant.CSVSource;
import org.openiam.idm.srvc.csv.constant.UserFields;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

public class UserCSVParser extends AbstractCSVParser<User, UserFields>
		implements CSVParser<User> {

	@Override
	public ReconciliationObject<User> toReconciliationObject(User pu,
			List<AttributeMap> attrMap) {
		return this.toReconciliationObject(pu, attrMap, UserFields.class);
	}

	@Override
	protected void putValueInDTO(User user, UserFields field, String objValue) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		switch (field) {
		case birthdate:
			try {
				user.setBirthdate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setBirthdate(null);
			}
			break;
		case companyId:
			user.setCompanyId(objValue);
			break;
		case companyOwnerId:
			user.setCompanyOwnerId(objValue);
			break;
		case createDate:
			try {
				user.setCreateDate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setCreateDate(null);
			}
			break;
		case createdBy:
			user.setCreatedBy(objValue);
			break;
		case departmentCD:
		case deptCd:
			user.setDeptCd(objValue);
			break;
		case deptName:
			user.setDeptName(objValue);
			break;
		case employeeId:
			user.setEmployeeId(objValue);
			break;
		case employeeType:
			user.setEmployeeType(objValue);
			break;
		case firstName:
			user.setFirstName(objValue);
			break;
		case jobCode:
			user.setJobCode(objValue);
			break;
		case lastName:
			user.setLastName(objValue);
			break;
		case lastUpdate:
			try {
				user.setLastUpdate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setLastUpdate(null);
			}
			break;
		case lastUpdatedBy:
			user.setLastUpdatedBy(objValue);
			break;
		case locationCd:
			user.setLocationCd(objValue);
			break;
		case locationName:
			user.setLocationName(objValue);
			break;
		case managerId:
			user.setManagerId(objValue);
			break;
		case metadataTypeId:
			user.setMetadataTypeId(objValue);
			break;
		case classification:
			user.setClassification(objValue);
			break;
		case middleInit:
			user.setMiddleInit(objValue);
			break;
		case prefix:
			user.setPrefix(objValue);
			break;
		case sex:
			user.setSex(objValue);
			break;
		case status:
			user.setStatus(Enum.valueOf(UserStatusEnum.class,
					objValue.toUpperCase()));
			break;
		case secondaryStatus:
			user.setSecondaryStatus(Enum.valueOf(UserStatusEnum.class,
					objValue.toUpperCase()));
			break;
		case suffix:
			user.setSuffix(objValue);
			break;
		case title:
			user.setTitle(objValue);
			break;
		case uid:
		case userId:
			user.setUserId(objValue);
			break;
		case userTypeInd:
			user.setUserTypeInd(objValue);
			break;
		case userNotes:
		case emailAddresses:
		case userAttributes:
			break;
		case division:
			user.setDivision(objValue);
			break;
		case costCenter:
			user.setCostCenter(objValue);
			break;
		case startDate:
			try {
				user.setStartDate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setStartDate(null);
			}
			break;
		case lastDate:
			try {
				user.setLastDate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setLastDate(null);
			}
			break;
		case mailCode:
			user.setMailCode(objValue);
			break;
		case nickname:
			user.setNickname(objValue);
			break;
		case maidenName:
			user.setMaidenName(objValue);
			break;
		case passwordTheme:
			user.setPasswordTheme(objValue);
			break;
		case country:
			user.setCountry(objValue);
			break;
		case bldgNum:
			user.setBldgNum(objValue);
			break;
		case streetDirection:
			user.setStreetDirection(objValue);
			break;
		case suite:
			user.setSuite(objValue);
			break;
		case postalAddress:
		case address1:
			user.setAddress1(objValue);
			break;
		case address2:
			user.setAddress2(objValue);
			break;
		case address3:
			user.setAddress3(objValue);
			break;
		case address4:
			user.setAddress4(objValue);
			break;
		case address5:
			user.setAddress5(objValue);
			break;
		case address6:
			user.setAddress6(objValue);
			break;
		case address7:
			user.setAddress7(objValue);
			break;
		case city:
			user.setCity(objValue);
			break;
		case state:
			user.setState(objValue);
			break;
		case postalCd:
		case postalCode:
			user.setPostalCd(objValue);
			break;
		case mail:
		case email:
		case emailAddress:
			user.setEmail(objValue);
			break;
		case showInSearch:
			try {
				user.setShowInSearch(Integer.valueOf(objValue));
			} catch (Exception e) {
				user.setShowInSearch(null);
			}
			break;
		case delAdmin:
			try {
				user.setDelAdmin(Integer.valueOf(objValue));
			} catch (Exception e) {
				user.setDelAdmin(null);
			}
			break;
		case areaCd:
			user.setAreaCd(objValue);
			break;
		case countryCd:
			user.setCountryCd(objValue);
			break;
		case telephoneNumber:
		case phoneNbr:
			user.setPhoneNbr(objValue);
			break;
		case phoneExt:
			user.setPhoneExt(objValue);
			break;
		case principalList:
		case phones:
		case supervisor:
			break;
		case alternateContactId:
			user.setAlternateContactId(objValue);
			break;
		case securityDomain:
			user.setSecurityDomain(objValue);
			break;
		case userOwnerId:
			user.setUserOwnerId(objValue);
			break;
		case datePasswordChanged:
			try {
				user.setDatePasswordChanged(sdf.parse(objValue));
			} catch (Exception e) {
				user.setDatePasswordChanged(null);
			}
			break;
		case dateChallengeRespChanged:
			try {
				user.setDateChallengeRespChanged(sdf.parse(objValue));
			} catch (Exception e) {
				user.setDateChallengeRespChanged(null);
			}
			break;
		case DEFAULT:
			break;
		default:
			break;

		}

	}

	@Override
	protected String putValueIntoString(User user, UserFields field) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String objValue = "";
		switch (field) {
		case birthdate:
			objValue = user.getBirthdate() == null ? "" : toString(sdf
					.format(user.getBirthdate()));
			break;
		case companyId:
			objValue = toString(user.getCompanyId());
			break;
		case companyOwnerId:
			objValue = toString(user.getCompanyOwnerId());
			break;
		case createDate:
			objValue = user.getCreateDate() == null ? "" : toString(sdf
					.format(user.getCreateDate()));
			break;
		case createdBy:
			objValue = toString(user.getCreatedBy());
			break;
		case deptCd:
		case departmentCD:
			objValue = toString(user.getDeptCd());
			break;
		case deptName:
			objValue = toString(user.getDeptName());
			break;
		case employeeId:
			objValue = toString(user.getEmployeeId());
			break;
		case employeeType:
			objValue = toString(user.getEmployeeType());
			break;
		case firstName:
			objValue = toString(user.getFirstName());
			break;
		case jobCode:
			objValue = toString(user.getJobCode());
			break;
		case lastName:
			objValue = toString(user.getLastName());
			break;
		case lastUpdate:
			objValue = user.getLastUpdate() == null ? "" : toString(sdf
					.format(user.getLastUpdate()));
			break;
		case lastUpdatedBy:
			objValue = toString(user.getLastUpdatedBy());
			break;
		case locationCd:
			objValue = toString(user.getLocationCd());
			break;
		case locationName:
			objValue = toString(user.getLocationName());
			break;
		case managerId:
			objValue = toString(user.getManagerId());
			break;
		case metadataTypeId:
			objValue = toString(user.getMetadataTypeId());
			break;
		case classification:
			objValue = toString(user.getClassification());
			break;
		case middleInit:
			objValue = toString(user.getMiddleInit());
			break;
		case prefix:
			objValue = toString(user.getPrefix());
			break;
		case sex:
			objValue = toString(user.getSex());
			break;
		case status:
			objValue = toString(user.getStatus());
			break;
		case secondaryStatus:
			objValue = toString(user.getSecondaryStatus());
			break;
		case suffix:
			objValue = toString(user.getSuffix());
			break;
		case title:
			objValue = toString(user.getTitle());
			break;
		case uid:
		case userId:
			objValue = toString(user.getUserId());
			break;
		case userTypeInd:
			objValue = toString(user.getUserTypeInd());
			break;
		case division:
			objValue = toString(user.getDivision());
			break;
		case costCenter:
			objValue = toString(user.getCostCenter());
			break;
		case startDate:
			objValue = user.getStartDate() == null ? "" : toString(sdf
					.format(user.getStartDate()));
			break;
		case lastDate:
			objValue = user.getLastDate() == null ? "" : toString(sdf
					.format(user.getLastDate()));
			break;
		case mailCode:
			objValue = toString(user.getMailCode());
			break;
		case nickname:
			objValue = toString(user.getNickname());
			break;
		case maidenName:
			objValue = toString(user.getMaidenName());
			break;
		case passwordTheme:
			objValue = toString(user.getPasswordTheme());
			break;
		case country:
			objValue = toString(user.getCountry());
			break;
		case bldgNum:
			objValue = toString(user.getBldgNum());
			break;
		case streetDirection:
			objValue = toString(user.getStreetDirection());
			break;
		case suite:
			objValue = toString(user.getSuite());
			break;
		case postalAddress:
		case address1:
			objValue = toString(user.getAddress1());
			break;
		case address2:
			objValue = toString(user.getAddress2());
			break;
		case address3:
			objValue = toString(user.getAddress3());
			break;
		case address4:
			objValue = toString(user.getAddress4());
			break;
		case address5:
			objValue = toString(user.getAddress5());
			break;
		case address6:
			objValue = toString(user.getAddress6());
			break;
		case address7:
			objValue = toString(user.getAddress7());
			break;
		case city:
			objValue = toString(user.getCity());
			break;
		case state:
			objValue = toString(user.getState());
			break;
		case postalCd:
		case postalCode:
			objValue = toString(user.getPostalCd());
			break;
		case mail:
		case email:
		case emailAddress:
			objValue = toString(user.getEmail());
			break;
		case showInSearch:
			objValue = toString(user.getShowInSearch());
			break;
		case delAdmin:
			objValue = toString(user.getDelAdmin());
			break;
		case areaCd:
			objValue = toString(user.getAreaCd());
			break;
		case countryCd:
			objValue = toString(user.getCountryCd());
			break;
		case telephoneNumber:
		case phoneNbr:
			objValue = toString(user.getPhoneNbr());
			break;
		case phoneExt:
			objValue = toString(user.getPhoneExt());
			break;
		case alternateContactId:
			objValue = toString(user.getAlternateContactId());
			break;
		case securityDomain:
			objValue = toString(user.getSecurityDomain());
			break;
		case userOwnerId:
			objValue = toString(user.getUserOwnerId());
			break;
		case datePasswordChanged:
			objValue = user.getDatePasswordChanged() == null ? ""
					: toString(sdf.format(user.getDatePasswordChanged()));
			break;
		case dateChallengeRespChanged:
			objValue = user.getDateChallengeRespChanged() == null ? ""
					: toString(sdf.format(user.getDateChallengeRespChanged()));
			break;
		case userAttributes:
		case principalList:
		case supervisor:
		case userNotes:
		case phones:
		case emailAddresses:
			break;
		case DEFAULT:
			objValue = toString("");
			break;
		default:
			break;
		}
		return objValue;
	}

	@Override
	public void add(ReconciliationObject<User> newObject,
			ManagedSys managedSys, List<AttributeMap> attrMapList,
			CSVSource source) throws Exception {
		appendObjectToCSV(newObject, managedSys, attrMapList, User.class,
				UserFields.class, true, source);
	}

	@Override
	public void delete(String principal, ManagedSys managedSys,
			List<AttributeMap> attrMapList, CSVSource source) throws Exception {
		List<ReconciliationObject<User>> users = this.getObjects(managedSys,
				attrMapList, source);
		Iterator<ReconciliationObject<User>> userIter = users.iterator();
		while (userIter.hasNext()) {
			ReconciliationObject<User> user = userIter.next();
			if (principal != null) {
				if (principal.equals(user.getPrincipal())) {
					userIter.remove();
				}
			}
		}
		updateCSV(users, managedSys, attrMapList, User.class, UserFields.class,
				false, source);
	}

	@Override
	public void update(ReconciliationObject<User> newUser,
			ManagedSys managedSys, List<AttributeMap> attrMapList,
			CSVSource source) throws Exception {
		List<ReconciliationObject<User>> users = this.getObjects(managedSys,
				attrMapList, source);
		List<ReconciliationObject<User>> newUsers = new ArrayList<ReconciliationObject<User>>(
				0);
		for (ReconciliationObject<User> user : users) {
			if (newUser.getPrincipal().equals(user.getPrincipal())) {
				newUsers.add(newUser);
			} else {
				newUsers.add(user);
			}
		}
		updateCSV(users, managedSys, attrMapList, User.class, UserFields.class,
				false, source);
	}

	@Override
	public Map<String, String> convertToMap(List<AttributeMap> attrMap,
			ReconciliationObject<User> obj) {
		return super.convertToMap(attrMap, obj, UserFields.class);
	}

	@Override
	public List<ReconciliationObject<User>> getObjects(ManagedSys managedSys,
			List<AttributeMap> attrMapList, CSVSource source) throws Exception {
		return getObjectList(managedSys, attrMapList, User.class,
				UserFields.class, source);
	}

	@Override
	public String getFileName(ManagedSys mngSys, CSVSource source) {
		return super.getFileName(mngSys, source);
	}

	@Override
	public String objectToString(List<String> head, Map<String, String> obj) {
		StringBuilder stb = new StringBuilder();
		for (String h : head) {
			stb.append(obj.get(h.trim()) == null ? "" : obj.get(h));
			stb.append(",");
		}
		stb.deleteCharAt(stb.length() - 1);
		return stb.toString();
	}

	@Override
	public String objectToString(List<String> head,
			List<AttributeMap> attrMapList, ReconciliationObject<User> u) {
		return this.objectToString(head, this.convertToMap(attrMapList, u));
	}

	@Override
	public Map<String, String> matchFields(List<AttributeMap> attrMap,
			ReconciliationObject<User> u, ReconciliationObject<User> o) {
		Map<String, String> res = new HashMap<String, String>(0);
		Map<String, String> one = this.convertToMap(attrMap, u);
		Map<String, String> two = this.convertToMap(attrMap, o);
		for (String field : one.keySet()) {

			if (one.get(field) == null && two.get(field) == null) {
				res.put(field, null);
				continue;
			}
			if (one.get(field) == null && two.get(field) != null) {
				res.put(field, two.get(field));
				continue;
			}
			if (one.get(field) != null && two.get(field) == null) {
				res.put(field, one.get(field));
				continue;
			}
			if (one.get(field) != null && two.get(field) != null) {
				String firstVal = one.get(field).replaceFirst("^0*", "").trim();
				String secondVal = two.get(field).replaceFirst("^0*", "")
						.trim();
				res.put(field, firstVal.equalsIgnoreCase(secondVal) ? secondVal
						: ("[" + firstVal + "][" + secondVal + "]"));
				continue;
			}
		}

		return res;
	}

}
