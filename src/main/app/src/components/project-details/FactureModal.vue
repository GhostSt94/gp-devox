<template>
  <!-- Modal -->
    <div class="modal fade" id="factureModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title" id="staticBackdropLabel">Ajouter facture</h5>
            <button @click="resetFields" id="clsFactureModal" type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body py-3">
            <div class="form-floating mb-3">
                <input v-model="date" type="date" class="form-control" id="floatingInput1" >
                <label for="floatingInput1">Date</label>
            </div>
            <div class="form-floating mb-3">
                <input v-model="montant" type="number" min="0" step="100" class="form-control" id="floatingInput2" >
                <label for="floatingInput2">Montant</label>
            </div>
            <div class="row mb-3">
                    <div class="col-3">
                        <label class="h5">Status :</label>
                    </div>
                    <div class="col-9">
                        <select v-model="status" class="form-select" aria-label="status">
                            <option value="prévu">Prévu</option>
                            <option value="envoyer">Envoyer</option>
                            <option value="payer">Payer</option>
                        </select>
                    </div>
            </div>
            <div class="mb-3">
                <label for="formFile" class="form-label">Fichier :</label>
                <input class="form-control" type="file" id="formFile" accept=".pdf">
            </div>
            <div v-if="err!=''" class="alert alert-danger">
                {{err}}
            </div>
        </div>
        <div class="modal-footer">
            <i v-if="loading" class="fa-solid fa-circle-notch fa-spin mx-3"></i>
            <i v-if="success" class="fa-solid fa-check text-success fa-lg"></i>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetFields">Annuler</button>
            <button type="button" class="btn btn-primary" @click="addFacture">Ajouter</button>
        </div>
        </div>
    </div>
    </div><Toast />
</template>

<script>
import axios from "axios";
import Toast from 'primevue/toast';
import {mapGetters} from "vuex"

export default {
    name:"FactureModal",
    components:{Toast},
    emits: ['get-factures'],
    data(){
        return{
            date:null,
            montant:0,
            status:"",
            id_project:this.$route.params.id,
            err:"",
            loading:false,
            success:false
        }
    },
    computed:mapGetters(['getApiUrl']),
    methods:{
        addFacture(){
            const file=document.querySelector("input[type='file']")
            // file.files[0]===undefined
            if(this.date===null || this.montant===0 ||this.status===""){
                this.err="Veuillez remplir tous les champs"
                return
            }
            // if(file.files[0].type!=='application/pdf'){
            //     this.err="Format fichier invalid"
            //     return
            // }
            this.success=false
            this.loading=true
            axios.post(`${this.getApiUrl}api/factures/`,{
                date:this.date,
                montant:this.montant,
                status:this.status,
                id_project:this.id_project
            })
            .then(res=>{
                if(res.status===200){
                    if(file.files[0]!==undefined){
                        this.addFile(file,res.data)
                    }else{
                        this.$toast.add({severity:'success', summary: "Facture ajouté", life: 2000})
                        this.resetFields(file)
                        this.$emit("get-factures")
                    }
                }else{
                    this.loading=false
                    this.err="error inserting facture"
                }
            })
            .catch(err=>{
                this.loading=false
                this.err=err.response.data
            })
        },
        addFile(file,id){
            var myFormData = new FormData();
            myFormData.append('file', file.files[0]);
            axios.post(`${this.getApiUrl}api/files/facture/${id}`,myFormData)
            .then(res=>{
                console.log(res.data)
                this.loading=false
                this.success=true
                this.resetFields(file)
                this.$emit('get-factures')
            })
            .catch(err=>{
                this.loading=false
                this.err=err.response.data
                axios.delete(`${this.getApiUrl}factures/${id}`)
            })
        },
        resetFields(file=null){
            this.date=null;
            this.montant=0;
            this.status="";
            if(file==null){
                file.value=""
                document.querySelector('#clsFactureModal').click()
            }
        }
    }
}
</script>

<style>

</style>