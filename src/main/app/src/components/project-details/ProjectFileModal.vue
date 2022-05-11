<template>
  <!-- Modal -->
    <div class="modal fade" id="ProjectFileModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title" id="staticBackdropLabel">Ajouter fichier</h5>
            <button id="clsModalFile" type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body py-3">
            <div class="mb-3">
                <input class="form-control" type="file" id="projectfile" accept=".pdf">
            </div>
            <div v-if="err!=''" class="alert alert-danger">
                {{err}}
            </div>
        </div>
        <div class="modal-footer">
            <i v-if="loading" class="fa-solid fa-circle-notch fa-spin mx-3"></i>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
            <button type="button" class="btn btn-primary" @click="addFacture">Ajouter</button>
        </div>
        </div>
    </div>
    </div>
</template>

<script>
import axios from "axios";
import {mapGetters} from 'vuex'
export default {
    name:"FactureModal",
    data(){
        return{
            id:this.$route.params.id,
            err:"",
            loading:false,
        }
    },
    computed:mapGetters(['getApiUrl']),
    methods:{
        addFacture(){
            const file=document.querySelector("input[id='projectfile']")
            if(file.files[0]===undefined){
                this.err="Veuillez Ajouter un fichier"
                return
            }
            if(file.files[0].type!=='application/pdf'){
                this.err="Format fichier invalid"
                return
            }
            this.err=""
            this.loading=true
            
            var myFormData = new FormData();
            myFormData.append('file', file.files[0]);
            axios.post(`${this.getApiUrl}api/files/project/${this.id}`,myFormData)
            .then(res=>{
                console.log(res.data)
                this.loading=false
                file.value=""
                document.querySelector("#clsModalFile").click()
                this.$emit('get-project')
            })
            .catch(err=>{
                console.error(err)
                this.err=err.response.data
            })
            
        }
    }
}
</script>

<style>

</style>