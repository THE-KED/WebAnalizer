function fetchAsyncResult() {
    var table = document.getElementById('prodTable');
    var loader =document.querySelector('#waiting--msg');
    loader.className="waiting--msg";
    fetch('/check/links')
        .then(response => response.json())
        .then(data=> {
            loader.className="undisplay";
            var i = 1;
            table.innerHTML = '<caption>Links validation</caption>\n' +
                '                <thead>\n' +
                '                <tr>\n' +
                '                    <td class="no">No</td>\n' +
                '                    <th scope="row">Link</th>\n' +
                '                    <td>Validation</td>\n' +
                '                </tr>\n' +
                '                </thead>\n';



            data.forEach((link) =>{
                var tr = document.createElement('tr');
                tr.innerHTML ='<td>' + i + '</td>' + '<td class="link--case">' + link.href + '</td>';
                if(link.comment==='Success')
                    tr.innerHTML+='<td class="success">' + link.comment + '</td>';
                else
                    tr.innerHTML+='<td class="failure">' + link.comment + '</td>';
                i++;
                table.appendChild(tr);
            });
        });
}
function dissmitAlert(event){
    var alertModal = document.querySelector(".alert");
    alertModal.className="undisplay";
}
function stopAlert(event){
    var alertModal = document.querySelector(".alert");
    var errMsg = document.querySelector(".alert p");

    if(errMsg.textContent===""){
        alertModal.className="undisplay";

    }
}


document.addEventListener('DOMContentLoaded', (event) => {
    var alertModal = document.querySelector(".alert");
    alertModal.addEventListener("animationend", dissmitAlert, false);
    alertModal.addEventListener("animationstart", stopAlert, false);


    console.log('DOM fully loaded and parsed');
    fetchAsyncResult();
});
