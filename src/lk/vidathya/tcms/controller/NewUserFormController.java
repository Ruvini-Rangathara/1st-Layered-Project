package lk.vidathya.tcms.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lk.vidathya.tcms.dto.StaffDTO;
import lk.vidathya.tcms.dto.UserDTO;
import lk.vidathya.tcms.service.ServiceFactory;
import lk.vidathya.tcms.service.ServiceType;
import lk.vidathya.tcms.service.custom.StaffService;
import lk.vidathya.tcms.service.custom.UserService;
import lk.vidathya.tcms.util.Navigation;
import lk.vidathya.tcms.util.RegExPatterns;
import lk.vidathya.tcms.util.Routes;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class NewUserFormController {


    public AnchorPane context;
    public StaffService staffService;
    public UserService userService;
    @FXML
    private Label lblDate;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblInvalidUsername;
    @FXML
    private Label lblInvalidPassword;
    @FXML
    private Button btnAddUser;
    @FXML
    private Label lblInvalidPasswordHint;
    @FXML
    private Label lblIncorrectReEnteredPassword;
    @FXML
    private ComboBox<String> cmbStaffId;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtReEnterPassword;
    @FXML
    private TextField txtPasswordHint;
    @FXML
    private TextField txtName;

    public void initialize() {
        userService = ServiceFactory.getInstance().getService(ServiceType.USER_SERVICE_IMPL);
        staffService = ServiceFactory.getInstance().getService(ServiceType.STAFF_SERVICE_IMPL);
        lblDate.setText(String.valueOf(LocalDate.now()));
        loadStaffId();

        lblInvalidUsername.setVisible(false);
        lblInvalidPassword.setVisible(false);
        lblIncorrectReEnteredPassword.setVisible(false);
        lblInvalidPasswordHint.setVisible(false);
    }

    private void loadStaffId() {
        try {
            ObservableList<String> staffIds = (ObservableList<String>) staffService.loadIdsToComboBox();
            cmbStaffId.setItems(staffIds);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddUserOnAction(ActionEvent event) {

        lblInvalidUsername.setVisible(false);
        lblInvalidPassword.setVisible(false);
        lblIncorrectReEnteredPassword.setVisible(false);
        lblInvalidPasswordHint.setVisible(false);

        boolean isUsernameMatched = RegExPatterns.getUsernamePattern().matcher(txtUsername.getText()).matches();
        boolean isPasswordMatched = RegExPatterns.getPasswordPattern().matcher(txtPassword.getText()).matches();
        boolean isPasswordHintMatched = RegExPatterns.getPasswordPattern().matcher(txtPasswordHint.getText()).matches();

        if (isUsernameMatched) {
            if (isPasswordMatched) {
                if (isPasswordHintMatched) {

                    if (txtPassword.getText().equals(txtPasswordHint.getText())) {
                        UserDTO userDTO = new UserDTO(String.valueOf(cmbStaffId.getValue()), txtUsername.getText(), txtPassword.getText(), txtPasswordHint.getText());

                        try {
                            boolean isAdd = userService.add(userDTO);
                            if (isAdd) {

                                Optional<ButtonType> choose = new Alert(Alert.AlertType.CONFIRMATION, "User Added Successfully!", ButtonType.OK, ButtonType.CANCEL).showAndWait();
                                if (choose.get() == ButtonType.OK) {
                                    try {
                                        Navigation.navigate(Routes.NEWUSER, context);
                                    } catch (IOException e) {
                                        new Alert(Alert.AlertType.ERROR, e + "").show();
                                    }
                                }

                            }


                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Password does not matched!").show();

                    }


                } else {
                    lblInvalidPasswordHint.setVisible(true);
                    txtPasswordHint.requestFocus();
                }
            } else {
                lblInvalidPassword.setVisible(true);
                txtPassword.requestFocus();
            }
        } else {
            lblInvalidUsername.setVisible(true);
            txtUsername.requestFocus();
        }

    }

    @FXML
    void btnCancelOnAction(ActionEvent event) throws IOException {
        Navigation.navigate(Routes.DASHBOARDVIEW, context);
    }

    @FXML
    void cmbStaffIdOnAction(ActionEvent event) {

        try {
            StaffDTO staffDTO = staffService.searchById(cmbStaffId.getValue());
            txtName.setText(staffDTO.getName());

            UserDTO userDTO = userService.searchById(cmbStaffId.getValue());
            if (userDTO != null) {
                txtUsername.setText(userDTO.getUsername());
                txtPassword.setText(userDTO.getPassword());
                txtPasswordHint.setText(userDTO.getPasswordHint());
                new Alert(Alert.AlertType.ERROR, "User Already Exists.").show();
                btnAddUser.setDisable(true);
            } else {
                btnAddUser.setDisable(false);
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e + "").show();
        }

    }

    @FXML
    void txtNameOnAction(ActionEvent event) {

    }

    @FXML
    void txtPasswordHintOnAction(ActionEvent event) {

    }

    @FXML
    void txtPasswordOnAction(ActionEvent event) {

    }

    @FXML
    void txtReEnterPasswordOnAction(ActionEvent event) {

    }

    @FXML
    void txtUsernameOnAction(ActionEvent event) {

    }

}
