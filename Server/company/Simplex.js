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
		case "Coupon":
			ancestor.component = Simplex.Coupon;
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
			this.uploadPrice = prefix + ":uploadPrice:" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div id="'+ref.root+'"><input type="radio"name="'+ref.model.peepHole+'"class="show-if"checked id="'+ref.model.open+'"/><div class="coupon-model shown-then has-shadow flashy"><div class="model-id items-on-top-left"><p id="'+ref.model.id+'">'+val.id+'</p></div><div class="model-title items-on-center"><h1 id="'+ref.model.title+'">'+val.title+'</h1></div><img id="'+ref.model.image+'"src="'+val.image+'"class="model-img"/><div class="model-type items-on-bottom-right"><h1 id="'+ref.model.type+'">'+val.type+'</h1></div><div class="model-price items-on-top-left"><span>$</span><span id="'+ref.model.price+'">'+val.price+'</span></div><div class="model-peephole clickable has-shadow minimal-padding"><p><label for="'+ref.model.close+'"title="press to see the coupon content">open</label></p></div></div><input type="radio"name="'+ref.model.peepHole+'"id="'+ref.model.close+'"class="show-if"/><div class="coupon-content has-shadow flashy shown-then"><div class="coupon-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><img id="'+ref.image+'"src="'+val.image+'"class="coupon-img"/><div class="coupon-title items-on-center"><h1 id="'+ref.title+'">'+val.title+'</h1></div><div  class="coupon-peephole clickable has-shadow minimal-padding"><p><label for="'+ref.model.open+'"title="press to minimaze the coupon content">close</label></p></div><div class="coupon-msg items-on-top-left"><p id="'+ref.message+'">'+val.message+'</p></div><div class="coupon-amount items-on-center-left"><p>Amount available:<input type="number"disabled="disabled"id="'+ref.amount+'"value="'+val.amount+'"/></p></div><div class="coupon-date items-on-center-left"><p>Start date:<input type="date"id="'+ ref.startDate +'"disabled="disabled"value="'+val.startDate+'"/></p><p>End date:<input type="date"id="'+ref.endDate+'"value="'+val.endDate+'"/></p></div><div class="coupon-type items-on-bottom-right"><div class="anchor"><input type="radio"name="'+ref.type+'"id="'+ref.food+'"value="Food"class="show-if"'+ ( ( val.type=="Food") ?"checked":"") +'/><h1 class="shown-then-inline">Food</h1><input type="radio"name="'+ref.type+'"id="'+ref.resturans+'"value="Resturans"class="show-if"'+((val.type=="Resturans")?"checked":"")+'/><h1 class="shown-then-inline">Resturans</h1><input type="radio"name="'+ref.type+'"id="'+ref.electricity+'"value="Electricity"class="show-if"'+((val.type=="Electricity")?"checked":"")+' /><h1 class="shown-then-inline">Electricity</h1><input type="radio"name="'+ref.type+'"id="'+ref.health+'"value="Health"class="show-if"'+((val.type=="Health")?"checked":"")+'/><h1 class="shown-then-inline">Health</h1><input type="radio"name="'+ref.type+'"id="'+ref.sports+'"value="Sports"class="show-if"'+((val.type=="Sports")?"checked":"")+'/><h1 class="shown-then-inline">Sports</h1><input type="radio"name="'+ref.type+'"id="'+ref.camping+'"value="Camping"class="show-if"'+((val.type=="Camping")?"checked":"")+'/><h1 class="shown-then-inline">Camping</h1><input type="radio"name="'+ref.type+'"id="'+ref.travelling+'"value="Travelling"class="show-if"'+((val.type=="Travelling")?"checked":"")+'/><h1 class="shown-then-inline">Travelling</h1></div></div><div class="coupon-buttons items-on-top-right"><button class="clickable"onclick="updateCoupon('+"Simplex.Coupon.get("+val.id+","+"'"+position+"'"+")"+')"title="press to save the changes">update</button><button class="clickable"onclick="removeCoupon('+"Simplex.Coupon.get("+val.id+","+"'"+position+"'"+")"+')"title="press to remove the coupon">delete</button></div><div class="coupon-price items-on-bottom-right"><button class="drop-down"disabled="disabled">pay $<span id="'+ref.price+'">'+val.price+'</span></button><div class="pop-down"><input type="number"min="0"id="'+ref.uploadPrice+'"value="'+val.price+'"/><button onclick="applyPrice('+"'"+ref.uploadPrice+"'"+','+"'"+ref.price+"'"+','+"'"+ref.model.price+"'"+')">Apply</button></div></div></div></div></div>';
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
			document.getElementById(ref.uploadPrice).value=val.price;
		},
		remove : function(id, position) {
			var element = document.getElementById(position + ":Coupon:" + id);
			if (element != null)
				document.getElementById(position).removeChild(element);
				Simplex.freeSpot(position, Simplex.Coupon, false);
		}
	}
}