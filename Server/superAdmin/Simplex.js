var Simplex = {
	getAncestor : function(path) {
		var id = path.slice(path.lastIndexOf(":") + 1);
		var ancestor ={};
		path = path.slice(0, path.lastIndexOf(":"));
		path = path.slice(0, path.lastIndexOf(":"));
		ancestor.position = path.slice(0, path.lastIndexOf(":"));
		switch (path.slice(path.lastIndexOf(":") + 1)) {
		case "Log":
			ancestor.component = Simplex.Log;
			ancestor.obj = ancestor.component.get(id, ancestor.position);
			return ancestor;
		case "Company":
			ancestor.component = Simplex.Company;
			ancestor.obj = ancestor.component.get(id, ancestor.position);
			return ancestor;
		case "Customer":
			ancestor.component = Simplex.Customer;
			ancestor.obj = ancestor.component.get(id, ancestor.position);
			return ancestor;
		case "Coupon":
			ancestor.component = Simplex.Coupon;
			ancestor.obj = ancestor.component.get(id, ancestor.position);
			return ancestor;
		case "PurchasedCoupon":
			ancestor.component = Simplex.PurchasedCoupon;
			ancestor.obj = ancestor.component.get(id, ancestor.position);
			return ancestor;
		default:
			return null;
		}
	},
	setSpot : function(position, component) {
		var spot = Simplex.getSpot(position, component);
		if (spot == null) {
			spot = {
				counter : 1,
				id : component.spots.length,
				position : position,
			};
			component.spots.push(spot);
		} else {
			spot.counter++;
		}
	},
	freeSpot : function(position, component, power) {
		var spot = Simplex.getSpot(position, component);
		if (spot != null && (spot.counter-- == 1 || power)) {
			component.spots.splice(spot.id, 1);
			for (i = spot.id; i < component.spots.length; i++) {
				component.spots[i].id -=1;
			}
		}
	},
	getSpot : function(position, component) {
		var i = component.spots.length;
		while (i-- > 0) {
			if (component.spots[i].position == position) {
				return component.spots[i];
			}
		}
		return null;
	},
	Log : {
		name : "Log",
		spots : [],
		ref : function(prefix, sufix) {
			prefix = prefix + ":Log";
			
			this.header = prefix + ":header:" + sufix;
			this.message = prefix + ":message:" + sufix;
			this.root = prefix + ":" + sufix;
			this.signature = prefix + ":signature:" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div id="'+ref.root+'"><h4 class="items-on-center emit"id="'+ref.header+'">'+val.header+'</h4><h5 class="items-on-center"id="'+ref.message+'">'+val.message+'</h5><h5 class="items-on-center">******************************************************************************</h5><h5 class="items-on-center"id="'+ref.signature+'">'+val.signature+'</h5></div>';
			document.getElementById(position).appendChild(container.firstChild);
			Simplex.setSpot(position, Simplex.Log);
		},
		get : function(id, position) {
			if (document.getElementById(position + ":Log:" + id) == null)
				return;
			var ref = new this.ref(position, id);
			var val = {};
			val.signature=document.getElementById(ref.signature).innerHTML;
			val.header=document.getElementById(ref.header).innerHTML;
			val.message=document.getElementById(ref.message).innerHTML;
			return val;
		},
		update : function(val, position) {
			if (document.getElementById(position + ":Log:" + val.id) == null)
				return;
			var ref = new this.ref(position, val.id);
			document.getElementById(ref.header).innerHTML=val.header;
			document.getElementById(ref.message).innerHTML=val.message;
			document.getElementById(ref.signature).innerHTML=val.signature;
		},
		remove : function(id, position) {
			var element = document.getElementById(position + ":Log:" + id);
			if (element != null)
				document.getElementById(position).removeChild(element);
				Simplex.freeSpot(position, Simplex.Log, false);
		}
	},
	Company : {
		name : "Company",
		spots : [],
		ref : function(prefix, sufix) {
			prefix = prefix + ":Company";
			
			this.close = prefix + ":close:" + sufix;
			this.compName = prefix + ":compName:" + sufix;
			this.coupons = prefix + ":coupons:" + sufix;
			this.email = prefix + ":email:" + sufix;
			this.id = prefix + ":id:" + sufix;
			this.open = prefix + ":open:" + sufix;
			this.password = prefix + ":password:" + sufix;
			this.peepHole = prefix + ":peepHole:" + sufix;
			this.root = prefix + ":" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div class="company"id="'+ref.root+'"><div class="comp-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><div class="comp-blank-left"></div><div class="comp-blank-right"></div><div class="comp-name items-on-center"><p id="'+ref.compName+'"class="title-scripte">'+val.compName+'</p></div><div class="comp-pswd items-on-center"><p id="'+ref.password+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.password+'</p></div><div class="comp-mail items-on-center"><p id="'+ref.email+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.email+'</p></div><div class="comp-buttons items-on-top-right"><button class="clickable"onclick="updateCompany('+"Simplex.Company.get("+val.id+","+"'"+position+"'"+")"+')"title="press to save the changes">update</button><button class="clickable"onclick="removeCompany('+"Simplex.Company.get("+val.id+","+"'"+position+"'"+")"+')"title="press to remove the company">remove</button></div><input type="radio"name="'+ref.peepHole+'"id="'+ref.close+'"class="show-if"checked /><div class="comp-peephole shown-then clickable has-shadow minimal-padding"><p><label for="'+ref.open+'"title="press to see the company'+"'"+'s coupons"onclick="getCompanyCoupons('+val.id+','+"'"+ref.coupons+"'"+')">open</label></p></div><input type="radio"name="'+ref.peepHole+'"id="'+ref.open+'"class="show-if"><div class="comp-peephole shown-then clickable has-shadow minimal-padding"><p><label for="'+ref.close+'"title="press to hide the company'+"'"+'s coupon">close</label></p></div><div class="shown-then comp-coupons"id="'+ref.coupons+'"></div></div>';
			document.getElementById(position).appendChild(container.firstChild);
			Simplex.setSpot(position, Simplex.Company);
		},
		get : function(id, position) {
			if (document.getElementById(position + ":Company:" + id) == null)
				return;
			var ref = new this.ref(position, id);
			var val = {};
			val.password=document.getElementById(ref.password).innerHTML;
			val.id=document.getElementById(ref.id).innerHTML;
			val.compName=document.getElementById(ref.compName).innerHTML;
			val.email=document.getElementById(ref.email).innerHTML;
			return val;
		},
		update : function(val, position) {
			if (document.getElementById(position + ":Company:" + val.id) == null)
				return;
			var ref = new this.ref(position, val.id);
			document.getElementById(ref.compName).innerHTML=val.compName;
			document.getElementById(ref.email).innerHTML=val.email;
			document.getElementById(ref.id).innerHTML=val.id;
			document.getElementById(ref.password).innerHTML=val.password;
		},
		remove : function(id, position) {
			var element = document.getElementById(position + ":Company:" + id);
			if (element != null)
				document.getElementById(position).removeChild(element);
				Simplex.freeSpot(position, Simplex.Company, false);
		}
	},
	Customer : {
		name : "Customer",
		spots : [],
		ref : function(prefix, sufix) {
			prefix = prefix + ":Customer";
			
			this.close = prefix + ":close:" + sufix;
			this.coupons = prefix + ":coupons:" + sufix;
			this.custName = prefix + ":custName:" + sufix;
			this.id = prefix + ":id:" + sufix;
			this.open = prefix + ":open:" + sufix;
			this.password = prefix + ":password:" + sufix;
			this.peepHole = prefix + ":peepHole:" + sufix;
			this.root = prefix + ":" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div class="customer"id="'+ref.root+'"><div class="cust-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><div class="cust-blank"></div><div class="cust-name items-on-center"><p id="'+ref.custName+'"class="title-scripte">'+val.custName+'</p></div><div class="cust-pswd items-on-center"><p id="'+ref.password+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.password+'</p></div><div class="cust-buttons items-on-top-right"><button class="clickable"onclick="updateCustomer('+"Simplex.Customer.get("+val.id+","+"'"+position+"'"+")"+')"title="press to save the changes">update</button><button class="clickable"onclick="removeCustomer('+"Simplex.Customer.get("+val.id+","+"'"+position+"'"+")"+')"title="press to remove the customer">remove</button></div><input type="radio"name="'+ref.peepHole+'"id="'+ref.close+'"class="show-if"checked /><div class="cust-peephole shown-then clickable has-shadow minimal-padding"><p><label for="'+ref.open+'"onclick="getCustomerCoupons('+val.id+','+"'"+ref.coupons+"'"+')"title="press to see the customer'+"'"+'s coupons">open</label></p></div><input type="radio"name="'+ref.peepHole+'"id="'+ref.open+'"class="show-if"/><div class="cust-peephole shown-then clickable has-shadow minimal-padding"><p><label for="'+ref.close+'"title="press to hide the customer'+"'"+'s coupons">close</label></p></div><div class="shown-then cust-coupons"id="'+ref.coupons+'"></div></div>';
			document.getElementById(position).appendChild(container.firstChild);
			Simplex.setSpot(position, Simplex.Customer);
		},
		get : function(id, position) {
			if (document.getElementById(position + ":Customer:" + id) == null)
				return;
			var ref = new this.ref(position, id);
			var val = {};
			val.password=document.getElementById(ref.password).innerHTML;
			val.id=document.getElementById(ref.id).innerHTML;
			val.custName=document.getElementById(ref.custName).innerHTML;
			return val;
		},
		update : function(val, position) {
			if (document.getElementById(position + ":Customer:" + val.id) == null)
				return;
			var ref = new this.ref(position, val.id);
			document.getElementById(ref.custName).innerHTML=val.custName;
			document.getElementById(ref.id).innerHTML=val.id;
			document.getElementById(ref.password).innerHTML=val.password;
		},
		remove : function(id, position) {
			var element = document.getElementById(position + ":Customer:" + id);
			if (element != null)
				document.getElementById(position).removeChild(element);
				Simplex.freeSpot(position, Simplex.Customer, false);
		}
	},
	Coupon : {
		name : "Coupon",
		spots : [],
		ref : function(prefix, sufix) {
			prefix = prefix + ":Coupon";
			this.model={};
			this.model.close = prefix + ":model.close:" + sufix;
			this.model.id = prefix + ":model.id:" + sufix;
			this.model.image = prefix + ":model.image:" + sufix;
			this.model.open = prefix + ":model.open:" + sufix;
			this.model.peepHole = prefix + ":model.peepHole:" + sufix;
			this.model.price = prefix + ":model.price:" + sufix;
			this.model.title = prefix + ":model.title:" + sufix;
			this.model.type = prefix + ":model.type:" + sufix;
			this.amount = prefix + ":amount:" + sufix;
			this.camping = prefix + ":camping:" + sufix;
			this.electricity = prefix + ":electricity:" + sufix;
			this.endDate = prefix + ":endDate:" + sufix;
			this.food = prefix + ":food:" + sufix;
			this.health = prefix + ":health:" + sufix;
			this.id = prefix + ":id:" + sufix;
			this.image = prefix + ":image:" + sufix;
			this.message = prefix + ":message:" + sufix;
			this.price = prefix + ":price:" + sufix;
			this.resturans = prefix + ":resturans:" + sufix;
			this.root = prefix + ":" + sufix;
			this.sports = prefix + ":sports:" + sufix;
			this.startDate = prefix + ":startDate:" + sufix;
			this.title = prefix + ":title:" + sufix;
			this.travelling = prefix + ":travelling:" + sufix;
			this.type = prefix + ":type:" + sufix;
			this.uploadImg = prefix + ":uploadImg:" + sufix;
			this.uploadPrice = prefix + ":uploadPrice:" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div id="'+ref.root+'"><input type="radio"name="'+ref.model.peepHole+'"class="show-if"checked id="'+ref.model.open+'"/><div class="coupon-model shown-then has-shadow flashy"><div class="model-id items-on-top-left"><p id="'+ref.model.id+'">'+val.id+'</p></div><div class="model-title items-on-center"><h1 id="'+ref.model.title+'">'+val.title+'</h1></div><img id="'+ref.model.image+'"src="'+val.image+'"class="model-img"/><div class="model-type items-on-bottom-right"><h1 id="'+ref.model.type+'">'+val.type+'</h1></div><div class="model-price items-on-top-left"><span>$</span><span id="'+ref.model.price+'">'+val.price+'</span></div><div class="model-peephole clickable has-shadow minimal-padding"><p><label for="'+ref.model.close+'"title="press to see the coupon content">open</label></p></div></div><input type="radio"name="'+ref.model.peepHole+'"id="'+ref.model.close+'"class="show-if"/><div class="coupon-content has-shadow flashy shown-then"><div class="coupon-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><img id="'+ref.image+'"src="'+val.image+'"class="coupon-img"/><div class="change-img drop-down items-on-top minimal-padding"><p>Change</p></div><div class="pop-down"><input type="text"id="'+ref.uploadImg+'"value="'+val.image+'"/><button onclick="applyImage('+"'"+ref.uploadImg+"'"+','+"'"+ref.image+"'"+', '+"'"+ref.model.image+"'"+')">Apply</button></div><div class="coupon-title items-on-center"><h1 id="'+ref.title+'">'+val.title+'</h1></div><div  class="coupon-peephole clickable has-shadow minimal-padding"><p><label for="'+ref.model.open+'"title="press to minimaze the coupon content">close</label></p></div><div class="coupon-msg items-on-top-left"><p id="'+ref.message+'"title="you can edit it (click on it)"contenteditable="true"spellcheck="false">'+val.message+'</p></div><div class="coupon-amount items-on-center-left"><p>Amount available:<input type="number"id="'+ref.amount+'"value="'+val.amount+'"/></p></div><div class="coupon-date items-on-center-left"><p>Start date:<input type="date"id="'+ ref.startDate +'"value="'+val.startDate+'"/></p><p>End date:<input type="date"id="'+ref.endDate+'"value="'+val.endDate+'"/></p></div><div class="coupon-type items-on-bottom-right"><div class="anchor"><input type="radio"name="'+ref.type+'"id="'+ref.food+'"value="Food"class="show-if"onclick="applyType('+"'"+'Food'+"'"+', '+"'"+ref.model.type+"'"+')"'+ ( ( val.type=="Food") ?"checked":"") +'/><h1 class="shown-then-inline drop-down">Food</h1><input type="radio"name="'+ref.type+'"id="'+ref.resturans+'"value="Resturans"onclick="applyType('+"'"+'Resturans'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Resturans")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Resturans</h1><input type="radio"name="'+ref.type+'"id="'+ref.electricity+'"value="Electricity"onclick="applyType('+"'"+'Electricity'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Electricity")?"checked":"")+' /><h1 class="shown-then-inline drop-down">Electricity</h1><input type="radio"name="'+ref.type+'"id="'+ref.health+'"value="Health"onclick="applyType('+"'"+'Health'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Health")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Health</h1><input type="radio"name="'+ref.type+'"id="'+ref.sports+'"value="Sports"onclick="applyType('+"'"+'Sports'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Sports")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Sports</h1><input type="radio"name="'+ref.type+'"id="'+ref.camping+'"value="Camping"onclick="applyType('+"'"+'Camping'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Camping")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Camping</h1><input type="radio"name="'+ref.type+'"id="'+ref.travelling+'"value="Travelling"onclick="applyType('+"'"+'Travelling'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Travelling")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Travelling</h1><div class="pop-up on-top flashy"><p><label for="'+ref.resturans+'">RESTURANS</label></p><p><label for="'+ref.electricity+'">ELECTRICITY</label></p><p><label for="'+ref.food+'">FOOD</label></p><p><label for="'+ref.health+'">HEALTH</label></p><p><label for="'+ref.sports+'">SPORTS</label></p><p><label for="'+ref.camping+'">CAMPING</label></p><p><label for="'+ref.travelling+'">TRAVELLING</label></p></div></div></div><div class="coupon-buttons items-on-top-right"><button class="clickable"onclick="updateCoupon('+"Simplex.Coupon.get("+val.id+","+"'"+position+"'"+")"+','+"Simplex.Coupon"+','+"'"+position+"'"+')"title="press to save the changes">update</button><button class="clickable"onclick="removeCoupon('+"Simplex.Coupon.get("+val.id+","+"'"+position+"'"+")"+','+"'"+position+"'"+')"title="press to remove the coupon">delete</button></div><div class="coupon-price items-on-bottom-right"><button class="drop-down"disabled="disabled">pay $<span id="'+ref.price+'">'+val.price+'</span></button><div class="pop-down"><input type="number"min="0"id="'+ref.uploadPrice+'"value="'+val.price+'"/><button onclick="applyPrice('+"'"+ref.uploadPrice+"'"+','+"'"+ref.price+"'"+','+"'"+ref.model.price+"'"+')">Apply</button></div></div></div></div></div>';
			document.getElementById(position).appendChild(container.firstChild);
			Simplex.setSpot(position, Simplex.Coupon);
		},
		get : function(id, position) {
			if (document.getElementById(position + ":Coupon:" + id) == null)
				return;
			var ref = new this.ref(position, id);
			var val = {};
			val.image=document.getElementById(ref.model.image).src;
			val.amount=document.getElementById(ref.amount).value;
			val.endDate=document.getElementById(ref.endDate).value;
			val.price=document.getElementById(ref.model.price).innerHTML;
			val.id=document.getElementById(ref.model.id).innerHTML;
			val.title=document.getElementById(ref.model.title).innerHTML;
			val.type=document.getElementById(ref.model.type).innerHTML;
			val.message=document.getElementById(ref.message).innerHTML;
			val.startDate=document.getElementById(ref.startDate).value;
			return val;
		},
		update : function(val, position) {
			if (document.getElementById(position + ":Coupon:" + val.id) == null)
				return;
			var ref = new this.ref(position, val.id);
			document.getElementById(ref.model.id).innerHTML=val.id;
			document.getElementById(ref.model.image).src=val.image;
			document.getElementById(ref.model.price).innerHTML=val.price;
			document.getElementById(ref.model.title).innerHTML=val.title;
			document.getElementById(ref.model.type).innerHTML=val.type;
			document.getElementById(ref.amount).value=val.amount;
			document.getElementById(ref.endDate).value=val.endDate;
			document.getElementById(ref.id).innerHTML=val.id;
			document.getElementById(ref.image).src=val.image;
			document.getElementById(ref.message).innerHTML=val.message;
			document.getElementById(ref.price).innerHTML=val.price;
			document.getElementById(ref.startDate).value=val.startDate;
			document.getElementById(ref.title).innerHTML=val.title;
			document.querySelectorAll('input[name="'+ref.type+'"]').forEach(function(element) { if (element.value==val.type) { element.checked = true; } });
			document.getElementById(ref.uploadImg).value=val.image;
			document.getElementById(ref.uploadPrice).value=val.price;
		},
		remove : function(id, position) {
			var element = document.getElementById(position + ":Coupon:" + id);
			if (element != null)
				document.getElementById(position).removeChild(element);
				Simplex.freeSpot(position, Simplex.Coupon, false);
		}
	},
	PurchasedCoupon : {
		name : "PurchasedCoupon",
		spots : [],
		ref : function(prefix, sufix) {
			prefix = prefix + ":PurchasedCoupon";
			this.model={};
			this.model.close = prefix + ":model.close:" + sufix;
			this.model.id = prefix + ":model.id:" + sufix;
			this.model.image = prefix + ":model.image:" + sufix;
			this.model.open = prefix + ":model.open:" + sufix;
			this.model.peepHole = prefix + ":model.peepHole:" + sufix;
			this.model.price = prefix + ":model.price:" + sufix;
			this.model.title = prefix + ":model.title:" + sufix;
			this.model.type = prefix + ":model.type:" + sufix;
			this.amount = prefix + ":amount:" + sufix;
			this.camping = prefix + ":camping:" + sufix;
			this.electricity = prefix + ":electricity:" + sufix;
			this.endDate = prefix + ":endDate:" + sufix;
			this.food = prefix + ":food:" + sufix;
			this.health = prefix + ":health:" + sufix;
			this.id = prefix + ":id:" + sufix;
			this.image = prefix + ":image:" + sufix;
			this.message = prefix + ":message:" + sufix;
			this.price = prefix + ":price:" + sufix;
			this.resturans = prefix + ":resturans:" + sufix;
			this.root = prefix + ":" + sufix;
			this.sports = prefix + ":sports:" + sufix;
			this.startDate = prefix + ":startDate:" + sufix;
			this.title = prefix + ":title:" + sufix;
			this.travelling = prefix + ":travelling:" + sufix;
			this.type = prefix + ":type:" + sufix;
			this.uploadImg = prefix + ":uploadImg:" + sufix;
			this.uploadPrice = prefix + ":uploadPrice:" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div id="'+ref.root+'"><input type="radio"name="'+ref.model.peepHole+'"class="show-if"checked id="'+ref.model.open+'"/><div class="coupon-model shown-then has-shadow flashy"><div class="model-id items-on-top-left"><p id="'+ref.model.id+'">'+val.id+'</p></div><div class="model-title items-on-center"><h1 id="'+ref.model.title+'">'+val.title+'</h1></div><img id="'+ref.model.image+'"src="'+val.image+'"class="model-img"/><div class="model-type items-on-bottom-right"><h1 id="'+ref.model.type+'">'+val.type+'</h1></div><div class="model-price items-on-top-left"><span>$</span><span id="'+ref.model.price+'">'+val.price+'</span></div><div class="model-peephole clickable has-shadow minimal-padding"><p><label for="'+ref.model.close+'"title="press to see the coupon content">open</label></p></div></div><input type="radio"name="'+ref.model.peepHole+'"id="'+ref.model.close+'"class="show-if"/><div class="coupon-content has-shadow flashy shown-then"><div class="coupon-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><img id="'+ref.image+'"src="'+val.image+'"class="coupon-img"/><div class="change-img drop-down items-on-top minimal-padding"><p>Change</p></div><div class="pop-down"><input type="text"id="'+ref.uploadImg+'"value="'+val.image+'"/><button onclick="applyImage('+"'"+ref.uploadImg+"'"+','+"'"+ref.image+"'"+', '+"'"+ref.model.image+"'"+')">Apply</button></div><div class="coupon-title items-on-center"><h1 id="'+ref.title+'">'+val.title+'</h1></div><div  class="coupon-peephole clickable has-shadow minimal-padding"><p><label for="'+ref.model.open+'"title="press to minimaze the coupon content">close</label></p></div><div class="coupon-msg items-on-top-left"><p id="'+ref.message+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.message+'</p></div><div class="coupon-amount items-on-center-left"><p>Amount available:<input type="number"id="'+ref.amount+'"value="'+val.amount+'"/></p></div><div class="coupon-date items-on-center-left"><p>Start date:<input type="date"id="'+ ref.startDate +'"value="'+val.startDate+'"/></p><p>End date:<input type="date"id="'+ref.endDate+'"value="'+val.endDate+'"/></p></div><div class="coupon-type items-on-bottom-right"><div class="anchor"><input type="radio"name="'+ref.type+'"id="'+ref.food+'"value="Food"class="show-if"onclick="applyType('+"'"+'Food'+"'"+', '+"'"+ref.model.type+"'"+')"'+ ( ( val.type=="Food") ?"checked":"") +'/><h1 class="shown-then-inline drop-down">Food</h1><input type="radio"name="'+ref.type+'"id="'+ref.resturans+'"value="Resturans"onclick="applyType('+"'"+'Resturans'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Resturans")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Resturans</h1><input type="radio"name="'+ref.type+'"id="'+ref.electricity+'"value="Electricity"onclick="applyType('+"'"+'Electricity'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Electricity")?"checked":"")+' /><h1 class="shown-then-inline drop-down">Electricity</h1><input type="radio"name="'+ref.type+'"id="'+ref.health+'"value="Health"onclick="applyType('+"'"+'Health'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Health")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Health</h1><input type="radio"name="'+ref.type+'"id="'+ref.sports+'"value="Sports"onclick="applyType('+"'"+'Sports'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Sports")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Sports</h1><input type="radio"name="'+ref.type+'"id="'+ref.camping+'"value="Camping"onclick="applyType('+"'"+'Camping'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Camping")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Camping</h1><input type="radio"name="'+ref.type+'"id="'+ref.travelling+'"value="Travelling"onclick="applyType('+"'"+'Travelling'+"'"+', '+"'"+ref.model.type+"'"+')"class="show-if"'+((val.type=="Travelling")?"checked":"")+'/><h1 class="shown-then-inline drop-down">Travelling</h1><div class="pop-up on-top flashy"><p><label for="'+ref.resturans+'">RESTURANS</label></p><p><label for="'+ref.electricity+'">ELECTRICITY</label></p><p><label for="'+ref.food+'">FOOD</label></p><p><label for="'+ref.health+'">HEALTH</label></p><p><label for="'+ref.sports+'">SPORTS</label></p><p><label for="'+ref.camping+'">CAMPING</label></p><p><label for="'+ref.travelling+'">TRAVELLING</label></p></div></div></div><div class="coupon-buttons items-on-top-right"><button class="clickable"title="press to save the changes"onclick="updateCoupon('+"Simplex.PurchasedCoupon.get("+val.id+","+"'"+position+"'"+")"+','+"Simplex.PurchasedCoupon"+','+"'"+position+"'"+')">update</button><button class="clickable"title="press to remove the coupon from the customer"onclick="removeCouponFromCustomer('+"Simplex.getAncestor("+"'"+position+"'"+")"+'.obj.id,'+val.id+','+"'"+position+"'"+')">cashback</button></div><div class="coupon-price items-on-bottom-right"><button class="drop-down"disabled="disabled">pay $<span id="'+ref.price+'">'+val.price+'</span></button><div class="pop-down"><input type="number"min="0"id="'+ref.uploadPrice+'"value="'+val.price+'"/><button onclick="applyPrice('+"'"+ref.uploadPrice+"'"+','+"'"+ref.price+"'"+','+"'"+ref.model.price+"'"+')">Apply</button></div></div></div></div></div>';
			document.getElementById(position).appendChild(container.firstChild);
			Simplex.setSpot(position, Simplex.PurchasedCoupon);
		},
		get : function(id, position) {
			if (document.getElementById(position + ":PurchasedCoupon:" + id) == null)
				return;
			var ref = new this.ref(position, id);
			var val = {};
			val.image=document.getElementById(ref.model.image).src;
			val.amount=document.getElementById(ref.amount).value;
			val.endDate=document.getElementById(ref.endDate).value;
			val.price=document.getElementById(ref.model.price).innerHTML;
			val.id=document.getElementById(ref.model.id).innerHTML;
			val.title=document.getElementById(ref.model.title).innerHTML;
			val.type=document.getElementById(ref.model.type).innerHTML;
			val.message=document.getElementById(ref.message).innerHTML;
			val.startDate=document.getElementById(ref.startDate).value;
			return val;
		},
		update : function(val, position) {
			if (document.getElementById(position + ":PurchasedCoupon:" + val.id) == null)
				return;
			var ref = new this.ref(position, val.id);
			document.getElementById(ref.model.id).innerHTML=val.id;
			document.getElementById(ref.model.image).src=val.image;
			document.getElementById(ref.model.price).innerHTML=val.price;
			document.getElementById(ref.model.title).innerHTML=val.title;
			document.getElementById(ref.model.type).innerHTML=val.type;
			document.getElementById(ref.amount).value=val.amount;
			document.getElementById(ref.endDate).value=val.endDate;
			document.getElementById(ref.id).innerHTML=val.id;
			document.getElementById(ref.image).src=val.image;
			document.getElementById(ref.message).innerHTML=val.message;
			document.getElementById(ref.price).innerHTML=val.price;
			document.getElementById(ref.startDate).value=val.startDate;
			document.getElementById(ref.title).innerHTML=val.title;
			document.querySelectorAll('input[name="'+ref.type+'"]').forEach(function(element) { if (element.value==val.type) { element.checked = true; } });
			document.getElementById(ref.uploadImg).value=val.image;
			document.getElementById(ref.uploadPrice).value=val.price;
		},
		remove : function(id, position) {
			var element = document.getElementById(position + ":PurchasedCoupon:" + id);
			if (element != null)
				document.getElementById(position).removeChild(element);
				Simplex.freeSpot(position, Simplex.PurchasedCoupon, false);
		}
	}
}