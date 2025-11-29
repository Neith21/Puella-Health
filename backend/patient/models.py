from django.db import models

class Patient(models.Model):
    GENDER_CHOICES = [
        ('Masculino', 'Masculino'),
        ('Femenino', 'Femenino'),
        ('gei', 'Prefiero No Decirlo'),
    ]
    
    patient_first_name = models.CharField(max_length=50)
    patient_last_name = models.CharField(max_length=50)
    patient_birth_date = models.DateField()
    patient_age = models.IntegerField()
    patient_email = models.EmailField(max_length=100, unique=True)
    patient_phone = models.CharField(max_length=15)
    patient_gender = models.CharField(max_length=15, choices=GENDER_CHOICES)
    record_active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.patient_first_name} {self.patient_last_name}"

    class Meta:
        db_table = 'patient'
        verbose_name = 'Patient'
        verbose_name_plural = 'Patients'