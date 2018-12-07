/**
 * 
 */

/*
 * Logs:
 */

var Logger = {
	logs : [],
	currentPos : 0,
	currentAction : "",
	sliceLog : function(response, component, prefix, sufix) {
		var log = response.splice(0, 1);
		Logger.addLog(log[0], component, prefix, sufix);
		return response;
	},
	addLog : function(log, component, prefix, sufix) {
		Logger.currentAction = log.status;
		log.id = 0;
		var currDate = new Date();
		log.signature += " " + currDate.toDateString() + " "
				+ currDate.toTimeString().slice(0, 8);
		if (log.status == "SUCCESS") {
			log.message = "";
		}
		if (component != undefined && prefix != undefined && sufix != undefined
				&& log.hashcode != undefined) {
			log.component = component;
			log.prefix = prefix;
			log.sufix = sufix;
			switch (component.name) {
			case "Company":
				markCompany(log.hashcode, log.prefix, log.sufix);
				break;
			case "Customer":
				markCustomer(log.hashcode, log.prefix, log.sufix);
				break;
			case "Coupon":
			case "PurchasedCoupon":
				markCoupon(component, log.hashcode, log.prefix, log.sufix);
				break;
			}
		}
		if (Logger.logs.length == 10) {
			if (Logger.logs[0].component != undefined) {
				if (Logger.logs[0].component.name == "ref") {
					document.getElementById(Logger.logs[0].component.ref).classList
							.remove("error");
				} else {
					clrErrors(Logger.logs[0].component, Logger.logs[0].prefix,
							Logger.logs[0].sufix);
				}
			}
			Logger.logs = Logger.logs.splice(1, 9);
		}
		Logger.logs.push(log);
		if (Logger.logs.length == 1) {
			Simplex.Log.create(log, "log");
			document.getElementById("removable").innerHTML = "X";
		} else {
			Simplex.Log.update(log, "log");
		}
		currentPos = Logger.logs.length - 1;
		document.getElementById("pos").innerHTML = currentPos + 1;
	},
	forward : function() {
		currentPos = (currentPos + 1 == Logger.logs.length) ? currentPos
				: ++currentPos;
		Simplex.Log.update(Logger.logs[currentPos], "log");
		document.getElementById("pos").innerHTML = currentPos + 1;
	},
	back : function() {
		currentPos = (currentPos == 0) ? currentPos : --currentPos;
		Simplex.Log.update(Logger.logs[currentPos], "log");
		document.getElementById("pos").innerHTML = currentPos + 1;
	},
	first : function() {
		currentPos = 0;
		Simplex.Log.update(Logger.logs[currentPos], "log");
		document.getElementById("pos").innerHTML = currentPos + 1;
	},
	last : function() {
		currentPos = Logger.logs.length - 1;
		Simplex.Log.update(Logger.logs[currentPos], "log");
		document.getElementById("pos").innerHTML = currentPos + 1;
	},
	remove : function() {
		if (Logger.logs[currentPos].component != undefined) {
			switch (Logger.logs[currentPos].component.name) {
			case "ref":
				document.getElementById(Logger.logs[currentPos].component.ref).classList
						.remove("error");
				break;
			default:
				clrErrors(Logger.logs[currentPos].component,
						Logger.logs[currentPos].prefix,
						Logger.logs[currentPos].sufix);
				break;
			}
		}
		if ((Logger.logs.length == currentPos + 1) && Logger.logs.length > 1) {
			Logger.logs = Logger.logs.slice(0, currentPos);
			currentPos = (currentPos == Logger.logs.length) ? --currentPos
					: currentPos;
			Simplex.Log.update(Logger.logs[currentPos], "log");
			document.getElementById("pos").innerHTML = currentPos + 1;
		} else if (Logger.logs.length > 1) {
			var start = Logger.logs.slice(0, currentPos);
			var end = Logger.logs.slice(currentPos + 1);
			Logger.logs = start.concat(end);
			Simplex.Log.update(Logger.logs[currentPos], "log");
		} else if (Logger.logs.length == 1) {
			Simplex.Log.remove(0, "log");
			Logger.logs = [];
			document.getElementById("pos").innerHTML = currentPos;
			document.getElementById("removable").innerHTML = "";
		}
	}
}

/*
 * Page:
 */

function addAllToPage() {
	if (this.readyState == 4 && this.status == 200) {
		var component = this.component;
		var position = this.position;
		clr(position);
		var response = Logger.sliceLog(JSON.parse(this.responseText));
		if (Logger.currentAction != "FAILURE") {
			response.forEach(function(element) {
				if (component.name == "Coupon"
						|| component.name == "PurchasedCoupon") {
					element.startDate = element.startDate.slice(0, 10);
					var days = element.startDate.slice(9, 10);
					element.startDate = element.startDate.slice(0, 9)
							+ (parseInt(days) + 1);
					element.endDate = element.endDate.slice(0, 10);
					days = element.endDate.slice(9, 10);
					element.endDate = element.endDate.slice(0, 9)
							+ (parseInt(days) + 1);
				}
				component.create(element, position);
			});
		}
	}
}

function addToPage() {
	if (this.readyState == 4 && this.status == 200) {
		var obj = Logger.sliceLog(JSON.parse(this.responseText))[0];
		if (Logger.currentAction != "FAILURE") {
			if (this.component.name == "Coupon"
					|| this.component.name == "PurchasedCoupon") {
				obj.startDate = obj.startDate.slice(0, 10);
				var days = obj.startDate.slice(9, 10);
				obj.startDate = obj.startDate.slice(0, 9)
						+ (parseInt(days) + 1);
				obj.endDate = obj.endDate.slice(0, 10);
				days = obj.endDate.slice(9, 10);
				obj.endDate = obj.endDate.slice(0, 9) + (parseInt(days) + 1);
			}
			this.component.create(obj, this.position);
			if (this.context != undefined) {
				if (this.context == "CreateCustomer") {
					resetCustomer();
					document.getElementById("abort-customer").checked = true;
					window.scrollTo(0, document.body.scrollHeight);
				} else if (this.context == "CreateCompany") {
					resetCompany();
					document.getElementById("abort-company").checked = true;
					window.scrollTo(0, document.body.scrollHeight);
				}
			}
		}
	}
}

function updPage(cashback, coupon) {
	if (cashback == true || (this.readyState == 4 && this.status == 200)) {
		if (typeof (cashback) == "object" || cashback == false) {
			/*
			 * Really dont know why, but it seems that XMLHttpRequest sends some
			 * parameters (an object) on it's own.
			 */
			Logger.sliceLog(JSON.parse(this.responseText), this.component,
					this.position, this.obj.id);
		}
		if (Logger.currentAction != "FAILURE" || cashback == true) {
			if (cashback == true || this.component.name == "Coupon"
					|| this.component.name == "PurchasedCoupon") {
				var obj;
				if (cashback == true) {
					obj = coupon;
				} else {
					obj = this.obj;
					obj.startDate = obj.startDate.toISOString().slice(0, 10);
					obj.endDate = obj.endDate.toISOString().slice(0, 10);
				}
				Simplex.Coupon.spots.forEach(function(spot) {
					Simplex.Coupon.update(obj, spot.position);
				});
				Simplex.PurchasedCoupon.spots.forEach(function(spot) {
					Simplex.PurchasedCoupon.update(obj, spot.position);
				});
			} else {
				this.component.update(this.obj, this.position);
			}
		}
	}
}

function delFromPage() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), this.component,
				this.position, this.id);
		if (Logger.currentAction != "FAILURE") {
			if (this.component.name == "Coupon") {
				var id = this.id;
				Simplex.Coupon.spots.forEach(function(spot) {
					Simplex.Coupon.remove(id, spot.position);
				});
				Simplex.PurchasedCoupon.spots.forEach(function(spot) {
					Simplex.PurchasedCoupon.remove(id, spot.position);
				});
			} else {
				if (this.cashback == true) {
					var obj = this.component.get(this.id, this.position);
					obj.amount = parseInt(obj.amount) + 1;
					updPage(true, obj);
				}
				this.component.remove(this.id, this.position);
			}
		}
	}
}

/*
 * Simplex spot's (memory management) and page refreshing:
 */

function clr(position) {
	document.getElementById(position).innerHTML = "";
	if (position == "listOfCoupons") {
		Simplex.freeSpot(position, Simplex.Coupon, true);
	} else if (position == "listOfCompanies") {
		Simplex.Company.spots = [];
		var coupons = Simplex.getSpot("listOfCoupons", Simplex.Coupon);
		Simplex.Coupon.spots = [];
		if (coupons != null) {
			coupons.id = 0;
			Simplex.Coupon.spots.push(coupons);
		}
	} else if (position == "listOfCustomers") {
		Simplex.Customer.spots = [];
		Simplex.PurchasedCoupon.spots = [];
	} else if (position.indexOf("listOfCompanies") != -1) {
		Simplex.freeSpot(position, Simplex.Coupon, true);
	} else if (position.indexOf("listOfCustomers") != -1) {
		Simplex.freeSpot(position, Simplex.PurchasedCoupon, true);
	}
}

function clrErrors(component, prefix, sufix) {
	var refs = new component.ref(prefix, sufix);
	if (document.getElementById(refs.root) != null) {
		switch (component.name) {
		case "Company":
			document.getElementById(refs.id).classList.remove("error");
			document.getElementById(refs.compName).classList.remove("error");
			document.getElementById(refs.password).classList.remove("error");
			document.getElementById(refs.email).classList.remove("error");
			break;
		case "Customer":
			document.getElementById(refs.id).classList.remove("error");
			document.getElementById(refs.custName).classList.remove("error");
			document.getElementById(refs.password).classList.remove("error");
			break;
		case "Coupon":
		case "PurchasedCoupon":
			document.getElementById(refs.id).classList.remove("error");
			document.getElementById(refs.model.id).classList.remove("error");
			document.getElementById(refs.title).classList.remove("error");
			document.getElementById(refs.model.title).classList.remove("error");
			document.getElementById(refs.message).classList.remove("error");
			document.getElementById(refs.startDate).classList.remove("error");
			document.getElementById(refs.endDate).classList.remove("error");
			document.getElementById(refs.amount).classList.remove("error");
			document.getElementById(refs.price).classList.remove("error");
			document.getElementById(refs.model.price).classList.remove("error");
			document.getElementById(refs.image).classList.remove("error");
			document.getElementById(refs.model.image).classList.remove("error");
			document.querySelectorAll('input[name="' + refs.type + '"] +h1')
					.forEach(function(element) {
						element.classList.remove("error");
					});
			document.getElementById(refs.model.type).classList.remove("error");
		}
	}
}

/*
 * Coupons:
 */

var OKEY = 0;
var ID = 1 << 0;
var TITLE = 1 << 1;
var START_DATE = 1 << 2;
var END_DATE = 1 << 3;
var AMOUNT = 1 << 4;
var TYPE = 1 << 5;
var MESSAGE = 1 << 6;
var PRICE = 1 << 7;
var IMAGE = 1 << 8;

function validateCoupon(coupon, log) {
	var hashcode = OKEY;
	if (coupon.title.match(/^(&nbsp;\s?)*$/) || coupon.title == "") {
		hashcode |= TITLE;
		log.message += "The coupons's title is undefined. ";
	}
	if (coupon.amount == "0" || !coupon.amount) {
		hashcode |= AMOUNT;
		log.message += "The coupons's amount is undefined. ";
	}
	if (coupon.message.match(/^(&nbsp;\s?)*$/) || coupon.message == "") {
		hashcode |= MESSAGE;
		log.message += "The coupons's message is undefined. ";
	}
	if (coupon.price == "0" || !coupon.price) {
		hashcode |= PRICE;
		log.message += "The coupons's price is undefined. ";
	}
	if (hashcode != OKEY) {
		log.status = "REFUSED";
		log.hashcode = hashcode;
	}
}

function markCoupon(component, hashcode, prefix, sufix) {
	var refs = new component.ref(prefix, sufix);
	if (document.getElementById(refs.root) != null) {
		if ((hashcode & ID) != OKEY) {
			document.getElementById(refs.id).classList.add("error");
			document.getElementById(refs.model.id).classList.add("error");
		}
		if ((hashcode & TITLE) != OKEY) {
			document.getElementById(refs.title).classList.add("error");
			document.getElementById(refs.model.title).classList.add("error");
		}
		if ((hashcode & START_DATE) != OKEY) {
			document.getElementById(refs.startDate).classList.add("error");
		}
		if ((hashcode & END_DATE) != OKEY) {
			document.getElementById(refs.endDate).classList.add("error");
		}
		if ((hashcode & TYPE) != OKEY) {
			document.querySelectorAll('input[name="' + refs.type
					+ '"]:checked +h1')[0].classList.add("error");
			document.getElementById(refs.model.type).classList.add("error");
		}
		if ((hashcode & AMOUNT) != OKEY) {
			document.getElementById(refs.amount).classList.add("error");
		}
		if ((hashcode & MESSAGE) != OKEY) {
			document.getElementById(refs.message).classList.add("error");
		}
		if ((hashcode & PRICE) != OKEY) {
			document.getElementById(refs.price).classList.add("error");
			document.getElementById(refs.model.price).classList.add("error");
		}
		if ((hashcode & IMAGE) != OKEY) {
			document.getElementById(refs.image).classList.add("error");
			document.getElementById(refs.model.image).classList.add("error");
		}
	}
}

function getCoupon() {
	var input = document.getElementById("getCoupon");
	input.classList.remove("error");
	if (input.value == "") {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCoupon"
			},
			header : "There is a problem with the uploading the coupon.",
			message : "The coupon's id is undefined. Means your should set the desirable value of id first.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value < 1) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCoupon"
			},
			header : "There is a problem with the uploading the coupon with ID - "
					+ input.value + ".",
			message : "All coupon's ID must be greater than zero. Means the coupon ID is invalide and there is no coupon with same ID - "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value.match(/\./)) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCoupon"
			},
			header : "There is a problem with the uploading the coupon with ID - "
					+ input.value + ".",
			message : "All coupon's ID must be integer number. Means the coupon ID is invalide and there is no coupon with same ID - "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else {
		var request = new XMLHttpRequest();
		clr("listOfCoupons");
		request.component = Simplex.Coupon;
		request.position = "listOfCoupons";
		request.open("GET",
				"/Store/webapi/SuperAdmin/getCoupon/" + input.value, true);
		request.onreadystatechange = addToPage;
		request.send();
	}
}

function getAllCoupons() {
	var request = new XMLHttpRequest();
	request.component = Simplex.Coupon;
	request.position = "listOfCoupons";
	request.open("GET", "/Store/webapi/SuperAdmin/getAllCoupons", true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getCompanyCoupons(compId, position) {
	var request = new XMLHttpRequest();
	request.component = Simplex.Coupon;
	request.position = position;
	request.open("GET", "/Store/webapi/SuperAdmin/getCompanyCoupons/" + compId,
			true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getCustomerCoupons(custId, position) {
	var request = new XMLHttpRequest();
	request.component = Simplex.PurchasedCoupon;
	request.position = position;
	request.open("GET",
			"/Store/webapi/SuperAdmin/getCustomerCoupons/" + custId, true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getCouponsByType() {
	var request = new XMLHttpRequest();
	var type = document.getElementById("getCouponsByType").value;
	request.component = Simplex.Coupon;
	request.position = "listOfCoupons";
	request.open("GET", "/Store/webapi/SuperAdmin/getCouponsByType/" + type,
			true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function updateCoupon(coupon, component, position) {
	clrErrors(Simplex.Coupon, position, coupon.id);
	var log = {
		status : "",
		header : "There is a problem with the alternation of the coupon with title - "
				+ coupon.title + ".",
		message : "",
		signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
	};
	validateCoupon(coupon, log);
	if (log.status != "REFUSED") {
		var request = new XMLHttpRequest();
		coupon.startDate = new Date(coupon.startDate);
		coupon.endDate = new Date(coupon.endDate);
		request.obj = coupon;
		request.component = component;
		request.position = position;
		request.open("PUT", "/Store/webapi/SuperAdmin/updateCoupon", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = updPage;
		request.send(JSON.stringify(coupon));
	} else {
		log.message += "Please check all the coupon's values before you are pressing the update button.";
		Logger.addLog(log, Simplex.Coupon, position, coupon.id);
	}
}

function removeCoupon(coupon, position) {
	var request = new XMLHttpRequest();
	coupon.startDate = new Date(coupon.startDate);
	coupon.endDate = new Date(coupon.endDate);
	request.id = coupon.id;
	request.component = Simplex.Coupon;
	request.position = position;
	request.open("DELETE", "/Store/webapi/SuperAdmin/removeCoupon", true);
	request.setRequestHeader("Content-type", "application/json");
	request.onreadystatechange = delFromPage;
	request.send(JSON.stringify(coupon));
}

function removeCouponFromCustomer(custId, couponId, position) {
	var request = new XMLHttpRequest();
	request.id = couponId;
	request.component = Simplex.PurchasedCoupon;
	request.position = position;
	request.cashback = true;
	request.open("PUT", "/Store/webapi/SuperAdmin/removeCouponFromCustomer/"
			+ custId + "/" + couponId, true);
	request.onreadystatechange = delFromPage;
	request.send();
}

function applyImage(source, imgForCoupon, imgForModel) {
	var url = document.getElementById(source).value;
	document.getElementById(imgForCoupon).src = url;
	document.getElementById(imgForModel).src = url;
}

function applyType(type, modelType) {
	document.getElementById(modelType).innerHTML = type;
}

function applyPrice(source, couponPrice, modelPrice) {
	var price = document.getElementById(source).value;
	document.getElementById(couponPrice).innerHTML = price;
	document.getElementById(modelPrice).innerHTML = price;
}

function applyTitle(couponTitle, modelTitle) {
	var title = document.getElementById(couponTitle).innerHTML;
	document.getElementById(modelTitle).innerHTML = title;
}

/*
 * Companies and customers hashcode values:
 */

var NAME = 1 << 1;
var PASSWORD = 1 << 2;
var EMAIL = 1 << 3;

/*
 * Companies:
 */

function validateCompany(company, log) {
	var hashcode = OKEY;
	if (company.compName.match(/^(&nbsp;\s?)*$/) || (company.compName == "")) {
		hashcode |= NAME;
		log.message += "The company's name is undefined. ";
	}
	if (company.password.match(/^(&nbsp;\s?)*$/) || (company.password == "")) {
		hashcode |= PASSWORD;
		log.message += "The company's password is undefined. ";
	}
	if (company.email.match(/^(&nbsp;\s?)*$/) || (company.email == "")) {
		hashcode |= EMAIL;
		log.message += "The company's email is undefined. ";
	}
	if (hashcode != OKEY) {
		log.status = "REFUSED";
		log.hashcode = hashcode;
	}
}

function markCompany(hashcode, prefix, sufix) {
	var refs = new Simplex.Company.ref(prefix, sufix);
	if (document.getElementById(refs.root) != null) {
		if ((hashcode & NAME) != OKEY) {
			document.getElementById(refs.compName).classList.add("error");
		}
		if ((hashcode & PASSWORD) != OKEY) {
			document.getElementById(refs.password).classList.add("error");
		}
		if ((hashcode & EMAIL) != OKEY) {
			document.getElementById(refs.email).classList.add("error");
		}
	}
}

function createCompany() {
	clrErrors(Simplex.Company, "", 0);
	var company = Simplex.Company.get(0, "");
	var log = {
		status : "",
		header : "There is a problem with the creation of the company.",
		message : "",
		signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
	};
	validateCompany(company, log);
	if (log.status != "REFUSED") {
		var request = new XMLHttpRequest();
		request.name = company.compName;
		request.open("POST", "/Store/webapi/SuperAdmin/createCompany", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = getCompanyByName;
		request.send(JSON.stringify(company));
	} else {
		log.message += "Please set all the company's values before you are pressing the create button.";
		Logger.addLog(log, Simplex.Company, "", 0);
	}
}

function getCompany() {
	var input = document.getElementById("getCompany");
	input.classList.remove("error");
	if (input.value == "") {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCompany"
			},
			header : "There is a problem with the uploading the company.",
			message : "The company's id is undefined. Means your should set the desirable value of id first.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value <= 0) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCompany"
			},
			header : "There is a problem with the uploading the company with ID - "
					+ input.value + ".",
			message : "All company's ID must be greater than zero. Means the company ID is invalide and there is no company with same ID - "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value.match(/\./)) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCompany"
			},
			header : "There is a problem with the uploading the company with ID - "
					+ input.value + ".",
			message : "All company's ID must be integer number. Means the company ID is invalide and there is no company with same ID - "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else {
		var request = new XMLHttpRequest();
		clr("listOfCompanies");
		request.component = Simplex.Company;
		request.position = "listOfCompanies";
		request.open("GET", "/Store/webapi/SuperAdmin/getCompany/"
				+ input.value, true);
		request.onreadystatechange = addToPage;
		request.send();
	}
}

function getCompanyByName() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), Simplex.Company, "", 0);
		if (Logger.currentAction != "FAILURE") {
			var request = new XMLHttpRequest();
			request.component = Simplex.Company;
			request.position = "listOfCompanies";
			request.context = "CreateCompany";
			request.open("GET", "/Store/webapi/SuperAdmin/getCompanyByName/"
					+ this.name, true);
			request.onreadystatechange = addToPage;
			request.send();
		}
	}
}

function getAllCompanies() {
	var request = new XMLHttpRequest();
	request.component = Simplex.Company;
	request.position = "listOfCompanies";
	request.open("GET", "/Store/webapi/SuperAdmin/getAllCompanies", true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function updateCompany(company) {
	clrErrors(Simplex.Company, "listOfCompanies", company.id);
	var log = {
		status : "",
		header : "There is a problem with the alternation of the company with name - "
				+ company.compName + ".",
		message : "",
		signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
	};
	validateCompany(company, log);
	if (log.status != "REFUSED") {
		var request = new XMLHttpRequest();
		request.obj = company;
		request.component = Simplex.Company;
		request.position = "listOfCompanies";
		request.open("PUT", "/Store/webapi/SuperAdmin/updateCompany", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = updPage;
		request.send(JSON.stringify(company));
	} else {
		log.message += "Please check all the company's values before you are pressing the update button.";
		Logger.addLog(log, Simplex.Company, "listOfCompanies", company.id);
	}
}

function removeCompany(company) {
	var request = new XMLHttpRequest();
	request.id = company.id;
	request.component = Simplex.Company;
	request.position = "listOfCompanies";
	request.open("DELETE", "/Store/webapi/SuperAdmin/removeCompany", true);
	request.setRequestHeader("Content-type", "application/json");
	request.onreadystatechange = delFromPage;
	request.send(JSON.stringify(company));
}

function resetCompany() {
	clrErrors(Simplex.Company, "", 0);
	var company = {
		id : "0",
		compName : "",
		password : "",
		email : ""
	};
	Simplex.Company.update(company, "");
}

/*
 * Customers:
 */

function validateCustomer(customer, log) {
	var hashcode = OKEY;
	if (customer.custName.match(/^(&nbsp;\s?)*$/) || (customer.custName == "")) {
		hashcode |= NAME;
		log.message += "The customer's name is undefined. ";
	}
	if (customer.password.match(/^(&nbsp;\s?)*$/) || (customer.password == "")) {
		hashcode |= PASSWORD;
		log.message += "The customer's password is undefined. ";
	}
	if (hashcode != OKEY) {
		log.status = "REFUSED";
		log.hashcode = hashcode;
	}
}

function markCustomer(hashcode, prefix, sufix) {
	var refs = new Simplex.Customer.ref(prefix, sufix);
	if (document.getElementById(refs.root) != null) {
		if ((hashcode & ID) != OKEY) {
			document.getElementById(refs.id).classList.add("error");
		}
		if ((hashcode & NAME) != OKEY) {
			document.getElementById(refs.custName).classList.add("error");
		}
		if ((hashcode & PASSWORD) != OKEY) {
			document.getElementById(refs.password).classList.add("error");
		}
	}
}

function createCustomer() {
	clrErrors(Simplex.Customer, "", 0);
	var customer = Simplex.Customer.get(0, "");
	var log = {
		status : "",
		header : "There is a problem with the creation of the customer.",
		message : "",
		signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
	};
	validateCustomer(customer, log);
	if (log.status != "REFUSED") {
		var request = new XMLHttpRequest();
		request.name = customer.custName;
		request.open("POST", "/Store/webapi/SuperAdmin/createCustomer", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = getCustomerByName;
		request.send(JSON.stringify(customer));
	} else {
		log.message += "Please set all the customer's values before you are pressing the create button.";
		Logger.addLog(log, Simplex.Customer, "", 0);
	}
}

function getCustomer() {
	var input = document.getElementById("getCustomer");
	input.classList.remove("error");
	if (input.value == "") {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCustomer"
			},
			header : "There is a problem with the uploading the customer.",
			message : "The customer's id is undefined . Means your should set the desirable value of id first.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value <= 0) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCustomer"
			},
			header : "There is a problem with the uploading the customer with ID - "
					+ input.value + ".",
			message : "All customer's ID must be greater than zero. Means the customer ID is invalide and there is no customer with same ID - "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value.match(/\./)) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCustomer"
			},
			header : "There is a problem with the uploading the customer with ID - "
					+ input.value + ".",
			message : "All customer's ID must be integer number. Means the customer ID is invalide and there is no customer with same ID - "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else {
		var request = new XMLHttpRequest();
		clr("listOfCustomers");
		request.component = Simplex.Customer;
		request.position = "listOfCustomers";
		request.open("GET", "/Store/webapi/SuperAdmin/getCustomer/"
				+ input.value, true);
		request.onreadystatechange = addToPage;
		request.send();
	}
}

function getCustomerByName() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), Simplex.Customer, "", 0);
		if (Logger.currentAction != "FAILURE") {
			var request = new XMLHttpRequest();
			request.component = Simplex.Customer;
			request.position = "listOfCustomers";
			request.context = "CreateCustomer";
			request.open("GET", "/Store/webapi/SuperAdmin/getCustomerByName/"
					+ this.name, true);
			request.onreadystatechange = addToPage;
			request.send();
		}
	}
}

function getAllCustomers() {
	var request = new XMLHttpRequest();
	request.component = Simplex.Customer;
	request.position = "listOfCustomers";
	request.open("GET", "/Store/webapi/SuperAdmin/getAllCustomers", true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function updateCustomer(customer) {
	clrErrors(Simplex.Customer, "listOfCustomers", customer.id);
	var log = {
		status : "",
		header : "There is a problem with the alternation of the customer with name - "
				+ customer.custName + ".",
		message : "",
		signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
	};
	validateCustomer(customer, log);
	if (log.status != "REFUSED") {
		var request = new XMLHttpRequest();
		request.obj = customer;
		request.component = Simplex.Customer;
		request.position = "listOfCustomers";
		request.open("PUT", "/Store/webapi/SuperAdmin/updateCustomer", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = updPage;
		request.send(JSON.stringify(customer));
	} else {
		log.message += "Please check all the customer's values before you are pressing the update button.";
		Logger.addLog(log, Simplex.Customer, "listOfCustomers", customer.id);
	}
}

function removeCustomer(customer) {
	var request = new XMLHttpRequest();
	request.id = customer.id;
	request.component = Simplex.Customer;
	request.position = "listOfCustomers";
	request.open("DELETE", "/Store/webapi/SuperAdmin/removeCustomer", true);
	request.setRequestHeader("Content-type", "application/json");
	request.onreadystatechange = delFromPage;
	request.send(JSON.stringify(customer));
}

function resetCustomer() {
	clrErrors(Simplex.Customer, "", 0);
	var customer = {
		id : "0",
		custName : "",
		password : ""
	};
	Simplex.Customer.update(customer, "");
}

/*
 * Menu:
 */

var posY = 0;
var posX = 0;

function logout() {
	var request = new XMLHttpRequest();
	request.open("GET", "/Store/webapi/Common/logout/SuperAdmin", true);
	request.send();
	request.onreadystatechange = function() {
		window.location.href = "/Store/Login"
	};
}

function goTop() {
	posY = window.scrollY;
	posX = window.scrollX;
	window.scrollTo(0, 0);
}

function goBack() {
	window.scrollTo(posX, posY);
}