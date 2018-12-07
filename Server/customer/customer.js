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
			markCoupon(log.hashcode, log.prefix, log.sufix);
		}
		if (Logger.logs.length == 10) {
			if (Logger.logs[0].component != undefined) {
				if (Logger.logs[0].component.name == "ref") {
					document.getElementById(Logger.logs[0].component.ref).classList
							.remove("error");
				} else {
					clrErrors(Logger.logs[0].prefix, Logger.logs[0].sufix);
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
				clrErrors(Logger.logs[currentPos].prefix,
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
				element.startDate = element.startDate.slice(0, 10);
				var days = element.startDate.slice(9, 10);
				element.startDate = element.startDate.slice(0, 9)
						+ (parseInt(days) + 1);
				element.endDate = element.endDate.slice(0, 10);
				days = element.endDate.slice(9, 10);
				element.endDate = element.endDate.slice(0, 9)
						+ (parseInt(days) + 1);
				component.create(element, position);
			});
		}
	}
}

function updPage() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), Simplex.Coupon,
				"listOfCoupons", this.obj.id);
		if (Logger.currentAction != "FAILURE") {
			var obj = this.obj;
			obj.startDate = obj.startDate.toISOString().slice(0, 10);
			obj.endDate = obj.endDate.toISOString().slice(0, 10);
			Simplex.Coupon.update(obj, "listOfCoupons");
			if (overalFilter
					|| (priceFilter != -1 && parseInt(obj.price) <= parseInt(priceFilter))
					|| (typeFilter == obj.type)) {
				Simplex.PurchasedCoupon.create(obj, "listOfPurchasedCoupons");
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
	} else {
		Simplex.freeSpot(position, Simplex.PurchasedCoupon, true);
	}
}

function clrErrors(prefix, sufix) {
	var refs = new Simplex.Coupon.ref(prefix, sufix);
	if (document.getElementById(refs.root) != null) {
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

function markCoupon(hashcode, prefix, sufix) {
	var refs = new Simplex.Coupon.ref(prefix, sufix);
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

function purchaseCoupon(obj) {
	var request = new XMLHttpRequest();
	obj.amount -= 1;
	obj.startDate = new Date(obj.startDate);
	obj.endDate = new Date(obj.endDate);
	request.obj = obj;
	request.component = Simplex.Coupon;
	request.open("PUT", "/Store/webapi/Customer/purchaseCoupon", true);
	request.setRequestHeader("Content-type", "application/json");
	request.onreadystatechange = updPage;
	request.send(JSON.stringify(obj));
}

function getAllCoupons() {
	var request = new XMLHttpRequest();
	request.component = Simplex.Coupon;
	request.position = "listOfCoupons";
	request.open("GET", "/Store/webapi/Customer/getAllCoupons", true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getAllPurchasedCoupons() {
	var request = new XMLHttpRequest();
	request.component = Simplex.PurchasedCoupon;
	request.position = "listOfPurchasedCoupons";
	priceFilter = -1;
	typeFilter = null;
	overalFilter = true;
	request.open("GET", "/Store/webapi/Customer/getAllPurchasedCoupons", true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getAllPurchasedCouponsByType() {
	var request = new XMLHttpRequest();
	var type = document.getElementById("getPurchasedCouponsByType").value;
	request.component = Simplex.PurchasedCoupon;
	request.position = "listOfPurchasedCoupons";
	priceFilter = -1;
	typeFilter = type;
	overalFilter = false;
	request.open("GET", "/Store/webapi/Customer/getAllPurchasedCouponsByType/"
			+ type, true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getAllPurchasedCouponsByPrice() {
	var input = document.getElementById("getPurchasedCouponsByPrice");
	input.classList.remove("error");
	if (input.value == "") {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getPurchasedCouponsByPrice"
			},
			header : "There is a problem with the uploading all the coupons with the price lower or equal than the given price.",
			message : "The coupon's price is undefined. Means your should set the desirable value of the price first.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else if (input.value < 0) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getPurchasedCouponsByPrice"
			},
			header : "There is a problem with the uploading all the coupons with the price lower or equal to "
					+ input.value + ".",
			message : "All coupon's price must be greater or equal to zero. Means the coupon price is invalide and there is no coupon with the price lower or equal to "
					+ input.value + " in the database.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else {
		var request = new XMLHttpRequest();
		request.component = Simplex.PurchasedCoupon;
		request.position = "listOfPurchasedCoupons";
		priceFilter = input.value;
		typeFilter = null;
		overalFilter = false;
		request.open("GET",
				"/Store/webapi/Customer/getAllPurchasedCouponsByPrice/"
						+ input.value, true);
		request.onreadystatechange = addAllToPage;
		request.send();
	}
}

/*
 * Menu:
 */

var priceFilter = -1;
var typeFilter = null;
var overalFilter = false;

document.addEventListener("DOMContentLoaded", getAllCoupons);

function logout() {
	var request = new XMLHttpRequest();
	request.open("GET", "/Store/webapi/Common/logout/Customer", true);
	request.send();
	request.onreadystatechange = function() {
		window.location.href = "/Store/Login"
	};
}