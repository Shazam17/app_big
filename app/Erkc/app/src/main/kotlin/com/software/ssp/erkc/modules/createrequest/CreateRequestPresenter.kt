package com.software.ssp.erkc.modules.createrequest

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.common.UpdateRequestListAdapter
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmLocalImage
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import com.software.ssp.erkc.extensions.md5
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class CreateRequestPresenter @Inject constructor(view: ICreateRequestView) : RxPresenter<ICreateRequestView>(view), ICreateRequestPresenter {

    override var requestId: Int? = null

    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var requestRepository: RequestRepository
    @Inject
    lateinit var eventBus: Relay<Any, Any>

    override fun onViewLoadWithEditMode() {
        fetchRequestById(requestId!!)
    }

    override fun onViewAttached() {
        fetchDataForDropdowns()
    }

    private fun fetchDataForDropdowns() {
        subscriptions += realmRepository.fetchTypeHouseList()
                .subscribe { typeHouseList ->
                    val data = ArrayList<String>()
                    typeHouseList.forEach {
                        data.add(it.name!!)
                    }
                    view?.setTypeHouseSpinner(data)
                }
        // TODO add fetch request types
    }

    private fun fetchRequestById(id: Int) {
        subscriptions += realmRepository.fetchRequestById(id = id)
                .subscribe(
                        { realmRequest ->
                            view?.setFieldByRealmRequest(realmRequest = realmRequest)

                        },
                        { error ->
                            error.printStackTrace()
                        }
                )
    }

    override fun fetchCompanies(fias: String) {
        subscriptions += requestRepository.fetchCompaniesList(fias)
                .subscribe(
                        { it ->
                            val data = ArrayList<String>()
                            it.forEach {
                                data.add(it.name!!)
                            }
                            view?.setCompaniesSpinner(data)
                        },
                        { error ->
                            view?.onFetchCompaniesError()
                            error.printStackTrace()
                        }
                )
    }

    override fun fetchTypesRequest() {
        subscriptions += requestRepository.fetchTypesRequestList()
                .subscribe(
                        { it ->
                            val data = ArrayList<String>()
                            it.forEach {
                                data.add(it.name!!)
                            }
                            view?.setTypesRequestSpinner(data)
                        },
                        { error ->
                            view?.onFetchCompaniesError()
                            error.printStackTrace()
                        }
                )
    }

    override fun fetchDraftData(oldUUID: String) {
        subscriptions += realmRepository.fetchDraftRequestById(oldUUID)
                .subscribe(
                        {
                            view?.setDraftData(it)
                            deleteOldValue(oldUUID)
                        },
                        { error ->
                            error.printStackTrace()
                            view?.showMessage("Ошибка при загрузке черновика")
                        }
                )
    }

    override fun deleteOldValue(id: String) {
        subscriptions += realmRepository.deleteDraftRequestById(id)
                .subscribe()
    }

    override fun saveDraftRequest(realmDraft: RealmDraft) {
        subscriptions += realmRepository.saveDraftRequest(realmDraft)
                .subscribe(
                        {
                            view?.showMessage("Заявка сохранена в черновики")

                        },
                        { error ->
                            error.printStackTrace()
                            view?.showMessage("Ошибка сохранения черновика")
                        }
                )
    }

    override fun onTryAgainClicked(fias: String) {
        fetchCompanies(fias)
    }

    override fun onAddressFieldClick() {
        view?.navigateToSearchAddress()
    }

    override fun setEvent() {
        eventBus.call(UpdateRequestListAdapter())
    }

    override fun onCameraButtonClick() {
        view?.showCameraScreen()
    }

    override fun onGalleryButtonClick() {
        view?.showGalleryScreen()
    }

    override fun onPhotoClick(localImage: RealmLocalImage) {
        // TODO +++--+++
    }

    override fun generateRequest(title: String,
                                 address: String,
                                 fias: String,
                                 company: String,
                                 typeRequest: String,
                                 typeHouse: String,
                                 description: String,
                                 fio: String,
                                 phoneNum: String,
                                 images: List<RealmLocalImage>) {
        var addressId = 0
        var companyId = 0
        var typeRequestId = 0
        var typeHouseId = if (typeHouse.contains("Жилое")) {
            1
        } else {
            2
        }
        var email = ""
        var login=""
        subscriptions += realmRepository.fetchRequestAddressByName(address)
                .concatMap { data ->
                    addressId = data.id!!
                    Observable.just(null)
                }
                .concatMap { requestRepository.fetchCompaniesList(fias) }
                .concatMap { data ->
                    companyId = data.find { it.name == company }!!.id
                    Observable.just(null)
                }
                .concatMap { requestRepository.fetchTypesRequestList() }
                .concatMap { data ->
                    typeRequestId = data.find { it.name == typeRequest }!!.id!!
                    Observable.just(null)
                }
                .concatMap { realmRepository.fetchCurrentUser() }
                .concatMap { data ->
                    email = data.email
                    login=data.login
                    Observable.just(null)
                }
                .subscribe(
                        {
                            var tmpMd5=login+companyId+addressId+typeHouseId
                            var token=tmpMd5.md5()
                            var userName = fio.split(" ")
                            var data = mapOf<String,Any>(
                                    "company_id" to companyId,
                                    "house_id" to addressId,
                                    "premise_id" to typeHouseId,
                                    "last_name" to userName[0],
                                    "first_name" to userName[1],
                                    "middle_name" to userName[2],
                                    "email" to email,
                                    "phone" to phoneNum,
                                    "access_token" to token
                            )
                            registerUser(data,typeRequestId,email,phoneNum,title)
                        },
                        { error ->
                            error.printStackTrace()
                            view?.showMessage("Ошибка ")
                        }
                )

    }

    private fun registerUser(data: Map<String,Any>,typeRequestId: Int,email:String,phoneNum: String,title: String) {
        subscriptions += requestRepository.authUser(data)
                .subscribe(
                        {
                            view?.showMessage("Success ")
//                            createRequest(typeRequestId,email,phoneNum,title)

                        },
                        { error ->
                            error.printStackTrace()
                            view?.showMessage("Ошибка ")
                        }
                )

    }

//    private fun createRequest(typeRequestId: Int,email:String,phoneNum: String,title: String){
//        subscriptions+=requestRepository.createRequest(typeHouseId,email,phoneNum,title){
//
//        }
//
//    }

}