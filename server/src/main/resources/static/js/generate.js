function generate() {
	var res = "";
	var stringMatrix = document.getElementById('stringMatrix');
	var table = document.getElementById('table');
	var button = document.getElementById('button');
	table.innerHTML = "";
	var x = document.getElementById("fname").value;
	// Check the values are in fact numbers
	if ( (!isNaN(x))&&(x<=30)&&(x>0)) {
		table.style.visibility = "visible";
		button.style.visibility = "visible";
		// Iterate through rows
		for (var r = 0; r < x; r++) {
			// Create row element
			var tr = document.createElement('tr');
			// Iterate through columns
			for (var c  = 0; c < x; c++) {
				// Create cell element
				var td = document.createElement('td');
				// Setting some text content
				var input = document.createElement('input');
				input.setAttribute("type", "number");
				input.classList.add("tab");
				input.setAttribute("value", 0);
				input.setAttribute("name", r+"_"+c);
				input.addEventListener('keyup',translateTable);
				td.appendChild(input);
				//td.textContent = r + '_' + c;
				// Append cell to row
				tr.appendChild(td);
			}
			// Append row to table
			table.appendChild(tr);
		}
	}else{
		button.style.visibility = "hidden";
	}
}

function translateTable() {
	var x = 1;
	var y = 0;
	var string = document.getElementById("matrixString");
	document.getElementById("demo").innerHTML = "";
	var table = document.getElementById('table');
	for (var r = 0, n = table.rows.length; r < n; r++) {
		for (var c = 0, m = table.rows[r].cells.length; c < m; c++) {
			if ((table.rows[r].cells[c].children[0].value) == "") {
				document.getElementById("demo").innerHTML += y;
			}
			else if ((table.rows[r].cells[c].children[0].value) > 0) {
				document.getElementById("demo").innerHTML += x;
			}
			else if ((table.rows[r].cells[c].children[0].value) < 0) {
				document.getElementById("demo").innerHTML += y;
			}
			else{
				document.getElementById("demo").innerHTML += table.rows[r].cells[c].children[0].value;
			}
		}
	}
	string.value = document.getElementById("demo").innerHTML;
}