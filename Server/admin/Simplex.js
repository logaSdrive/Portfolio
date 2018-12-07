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
			
			this.compName = prefix + ":compName:" + sufix;
			this.email = prefix + ":email:" + sufix;
			this.id = prefix + ":id:" + sufix;
			this.password = prefix + ":password:" + sufix;
			this.root = prefix + ":" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div class="company"id="'+ref.root+'"><div class="comp-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><div class="comp-blank-left"></div><div class="comp-blank-right"></div><div class="comp-name items-on-center"><p class="title-scripte"id="'+ref.compName+'">'+val.compName+'</p></div><div class="comp-pswd items-on-center"><p id="'+ref.password+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.password+'</p></div><div class="comp-mail items-on-center"><p id="'+ref.email+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.email+'</p></div><div class="comp-buttons items-on-top-right"><button class="clickable"onclick="updateCompany('+"Simplex.Company.get("+val.id+","+"'"+position+"'"+")"+')"title="press to save the changes">update</button><button class="clickable"onclick="removeCompany('+"Simplex.Company.get("+val.id+","+"'"+position+"'"+")"+')"title="press to remove the company">remove</button></div></div>';
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
			
			this.custName = prefix + ":custName:" + sufix;
			this.id = prefix + ":id:" + sufix;
			this.password = prefix + ":password:" + sufix;
			this.root = prefix + ":" + sufix;
		},
		create : function(obj, position) {
			var val = obj;
			var ref = new this.ref(position, val.id);
			var container = document.createElement('div');
			container.innerHTML ='<div class="customer"id="'+ref.root+'"><div class="cust-id items-on-top-left"><p id="'+ref.id+'">'+val.id+'</p></div><div class="cust-blank"></div><div class="cust-name items-on-center"><p class="title-scripte"id="'+ref.custName+'">'+val.custName+'</p></div><div class="cust-pswd items-on-center"><p id="'+ref.password+'"contenteditable="true"title="you can edit it (click on it)"spellcheck="false">'+val.password+'</p></div><div class="cust-buttons items-on-top-right"><button class="clickable"onclick="updateCustomer('+"Simplex.Customer.get("+val.id+","+"'"+position+"'"+")"+')"title="press to save the changes">update</button><button class="clickable"onclick="removeCustomer('+"Simplex.Customer.get("+val.id+","+"'"+position+"'"+")"+')"title="press to remove the customer">remove</button></div></div>';
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
	}
}