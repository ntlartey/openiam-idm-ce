import java.util.ArrayList;
import java.util.Set;
import org.openiam.idm.srvc.continfo.dto.Phone;

def Set<Phone> phoneList = user.getPhones();
if (phoneList != null) {
	for (Phone p : phoneList) {
		if (p.name == "FAX" && p.phoneNbr != null && p.phoneNbr.length() > 0 ) {
			output = p.areaCd + "-" + p.phoneNbr;
			return;
		}
	
	}
	output=null;
}else {
	output=null;
}

