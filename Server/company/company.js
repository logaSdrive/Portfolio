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

function addToPage() {
	if (this.readyState == 4 && this.status == 200) {
		var obj = Logger.sliceLog(JSON.parse(this.responseText))[0];
		if (Logger.currentAction != "FAILURE") {
			obj.startDate = obj.startDate.slice(0, 10);
			var days = obj.startDate.slice(9, 10);
			obj.startDate = obj.startDate.slice(0, 9) + (parseInt(days) + 1);
			obj.endDate = obj.endDate.slice(0, 10);
			days = obj.endDate.slice(9, 10);
			obj.endDate = obj.endDate.slice(0, 9) + (parseInt(days) + 1);
			this.component.create(obj, this.position);
			if (this.context != undefined && this.context == "Create") {
				resetCoupon();
				document.getElementById("abort").checked = true;
				window.scrollTo(0, document.body.scrollHeight);
			}
		}
	}
}

function updPage() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), Simplex.Coupon,
				this.position, this.obj.id);
		if (Logger.currentAction != "FAILURE") {
			var obj = this.obj;
			obj.startDate = obj.startDate.toISOString().slice(0, 10);
			obj.endDate = obj.endDate.toISOString().slice(0, 10);
			Simplex.Coupon.update(obj, this.position);
		}
	}
}

function delFromPage() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), Simplex.Coupon,
				this.position, this.id);
		if (Logger.currentAction != "FAILURE") {
			Simplex.Coupon.remove(this.id, this.position);
		}
	}
}

/*
 * Simplex spot's (memory management) and page refreshing:
 */

function clr(position) {
	document.getElementById(position).innerHTML = "";
	Simplex.freeSpot(position, Simplex.Coupon, true);
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

function validateCoupon(coupon, log) {
	var hashcode = OKEY;
	if (coupon.title.match(/^(&nbsp;\s?)*$/) || coupon.title == "") {
		hashcode |= TITLE;
		log.message += "The coupons's title is undefined. ";
	}
	if (!coupon.startDate) {
		hashcode |= START_DATE;
		log.message += "The coupons's start date is undefined. ";
	}
	if (!coupon.endDate) {
		hashcode |= END_DATE;
		log.message += "The coupons's end date is undefined. ";
	}
	if (coupon.type == "Set a type...") {
		hashcode |= TYPE;
		log.message += "The coupons's type is undefined. ";
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

function createCoupon() {
	clrErrors("", 0);
	var log = {
		status : "",
		header : "There is a problem with the creation of the coupon.",
		message : "",
		signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
	};
	var coupon = Simplex.Coupon.get(0, "");
	validateCoupon(coupon, log);
	if (log.status != "REFUSED") {
		var request = new XMLHttpRequest();
		coupon.startDate = new Date(coupon.startDate);
		coupon.endDate = new Date(coupon.endDate);
		request.title = coupon.title;
		request.open("POST", "/Store/webapi/Company/createCoupon", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = getCouponByTitle;
		request.send(JSON.stringify(coupon));
	} else {
		log.message += "Please set all the coupon's values before you are pressing the create button.";
		Logger.addLog(log, Simplex.Coupon, "", 0);
	}
}

function getCouponByTitle() {
	if (this.readyState == 4 && this.status == 200) {
		Logger.sliceLog(JSON.parse(this.responseText), Simplex.Coupon, "", 0);
		if (Logger.currentAction != "FAILURE") {
			var request = new XMLHttpRequest();
			request.component = Simplex.Coupon;
			request.position = "listOfCoupons";
			request.context = "Create";
			request.open("GET", "/Store/webapi/Company/getCouponByTitle/"
					+ this.title, true);
			request.onreadystatechange = addToPage;
			request.send();
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
	} else if (input.value <= 0) {
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
		clr("listOfCoupons");
		var request = new XMLHttpRequest();
		request.component = Simplex.Coupon;
		request.position = "listOfCoupons";
		request.open("GET", "/Store/webapi/Company/getCoupon/" + input.value,
				true);
		request.onreadystatechange = addToPage;
		request.send();
	}
}

function getAllCoupons() {
	var request = new XMLHttpRequest();
	request.component = Simplex.Coupon;
	request.position = "listOfCoupons";
	request.open("GET", "/Store/webapi/Company/getAllCoupons", true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getCouponsByType() {
	var request = new XMLHttpRequest();
	var type = document.getElementById("getCouponsByType").value;
	request.component = Simplex.Coupon;
	request.position = "listOfCoupons";
	request.open("GET", "/Store/webapi/Company/getCouponsByType/" + type, true);
	request.onreadystatechange = addAllToPage;
	request.send();
}

function getCouponsByPrice() {
	var input = document.getElementById("getCouponsByPrice");
	input.classList.remove("error");
	if (input.value == "") {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCouponsByPrice"
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
				ref : "getCouponsByPrice"
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
		request.component = Simplex.Coupon;
		request.position = "listOfCoupons";
		request.open("GET", "/Store/webapi/Company/getCouponsByPrice/"
				+ input.value, true);
		request.onreadystatechange = addAllToPage;
		request.send();
	}
}

function getCouponsByDate() {
	var input = document.getElementById("getCouponsByDate");
	input.classList.remove("error");
	if (!input.value) {
		input.classList.add("error");
		var log = {
			status : "REFUSED",
			component : {
				name : "ref",
				ref : "getCouponsByDate"
			},
			header : "There is a problem with the collecting all the coupon until specified end date.",
			message : "The date is undefined. Means your should set the desirable date first.",
			signature : "signature: the operation was not send to the execution (means it has not specified signature value)."
		};
		Logger.addLog(log);
	} else {
		var request = new XMLHttpRequest();
		date = (new Date(input.value)).valueOf();
		request.component = Simplex.Coupon;
		request.position = "listOfCoupons";
		request.open("GET", "/Store/webapi/Company/getCouponsByDate/" + date,
				true);
		request.onreadystatechange = addAllToPage;
		request.send();
	}
}

function updateCoupon(coupon) {
	clrErrors("listOfCoupons", coupon.id);
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
		request.component = Simplex.Coupon;
		request.position = "listOfCoupons";
		request.open("PUT", "/Store/webapi/Company/updateCoupon", true);
		request.setRequestHeader("Content-type", "application/json");
		request.onreadystatechange = updPage;
		request.send(JSON.stringify(coupon));
	} else {
		log.message += "Please check all the coupon's values before you are pressing the update button.";
		Logger.addLog(log, Simplex.Coupon, "listOfCoupons", coupon.id);
	}
}

function removeCoupon(coupon) {
	var request = new XMLHttpRequest();
	coupon.startDate = new Date(coupon.startDate);
	coupon.endDate = new Date(coupon.endDate);
	request.id = coupon.id;
	request.component = Simplex.Coupon;
	request.position = "listOfCoupons";
	request.open("DELETE", "/Store/webapi/Company/removeCoupon", true);
	request.setRequestHeader("Content-type", "application/json");
	request.onreadystatechange = delFromPage;
	request.send(JSON.stringify(coupon));
}

function resetCoupon() {
	clrErrors("", 0);
	var coupon = {
		id : 0,
		title : "",
		message : "",
		image : "/Store/example.png",
		price : 0,
		amount : "0",
		startDate : "YYYY-MM-DD",
		endDate : "YYYY-MM-DD",
		type : "Set a type..."
	};
	Simplex.Coupon.update(coupon, "");
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
 * Menu:
 */

var posY = 0;
var posX = 0;

function logout() {
	var request = new XMLHttpRequest();
	request.open("GET", "/Store/webapi/Common/logout/Company", true);
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